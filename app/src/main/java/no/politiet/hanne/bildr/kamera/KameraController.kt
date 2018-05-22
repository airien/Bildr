package no.politiet.hanne.bildr.kamera

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.hardware.camera2.*
import android.hardware.camera2.CameraAccessException
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.Display
import android.view.Surface
import android.view.TextureView
import no.politiet.hanne.bildr.R
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class KameraController(private val textureView: TextureView, private val kameraEventListener: KameraEventListener, activity : Activity) {
    private var startet : Boolean = false
    private var kameraId: String? = null
    private var kameraSesjon: CameraCaptureSession? = null
    private var kamera : CameraDevice? = null
    private var bakgrunnshandler: Handler? = null
    private var bakgrunnstrad: HandlerThread? = null
    private var forhandsvisningStorrelse: Size? = null
    private var omrade: Surface? = null
    private val kameraLaas = Semaphore(1)
    private val kameraHandterer: CameraManager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val defaultDisplay : Display = activity.windowManager.defaultDisplay

    private  val captureCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession?) {
            kameraLaas.release()
            kameraSesjon = session
        }

        override fun onConfigured(session: CameraCaptureSession?) {
            kameraLaas.release()
            kameraSesjon = session
            oppdaterKameraState(Size(0,0))
        }
    }
    private val stateCallback  = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice?) {
            kameraLaas.release()
            kamera = cameraDevice
            oppdaterKameraState(forhandsvisningStorrelse!!)
        }
        override fun onDisconnected(cameraDevice: CameraDevice?) {
            kameraLaas.release()
            kamera = cameraDevice
            kamera!!.close()
            kamera = null
        }
        override fun onError(cameraDevice: CameraDevice?, error: Int) {
            kameraLaas.release()
            kamera = cameraDevice
            kamera!!.close()
            kamera = null
            kameraEventListener.onFeil(error)
        }
    }
    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
            configureTransform(width, height)
        }
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            oppdaterKameraState(Size(width,height))
        }
        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {}
        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {return true }
    }
    fun restartKamera(kamera_soker:TextureView) {
        apneBakgrunnstrad()
        if (kamera_soker.isAvailable) {
            oppdaterKameraState(Size(kamera_soker.width,kamera_soker.height))
        } else {
            kamera_soker.surfaceTextureListener = surfaceTextureListener
        }
    }
    private fun oppdaterKameraState(size: Size) {
        when {
            kamera == null -> {
                apneKamera(size)
                return
            }
            kameraSesjon == null -> {
                apneKameraSesjon()
                return
            }
            !startet -> startForhandsvisning()
        }
    }
    private fun apneKamera(size: Size) {

        if (kameraEventListener.checkPermission()) {
            if (!kameraLaas.tryAcquire(10000, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }

            setupKamera(size.width, size.height)
            configureTransform(size.width, size.height)
            kameraHandterer.openCamera(kameraId, stateCallback, bakgrunnshandler)
            kameraLaas.release()
        } else kameraEventListener.onFeil(R.string.apneKameraFeil)
    }
    private fun apneKameraSesjon() {
        kameraLaas.acquire()
        try {
            omrade = Surface(textureView.surfaceTexture)
            kamera!!.createCaptureSession(Collections.singletonList(omrade), captureCallback, bakgrunnshandler)
            return
        } catch (e: CameraAccessException) {
            kameraLaas.release()
            e.printStackTrace()
        }
    }
    private fun startForhandsvisning() {
        Handler().postDelayed({
            try {
                startet = true
                val builder: CaptureRequest.Builder = kameraSesjon!!.device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                builder.addTarget(omrade)
                kameraSesjon!!.setRepeatingRequest(builder.build(), null, bakgrunnshandler)
            } catch (e: CameraAccessException) {
                startet = false
                if (e.reason == CameraAccessException.CAMERA_DISCONNECTED)
                    return@postDelayed
                lukkKamera()
                oppdaterKameraState( forhandsvisningStorrelse!!)
            } catch (e: Exception) {
                startet = false
                lukkKamera()
                oppdaterKameraState( forhandsvisningStorrelse!!)
            }
        }, 500)
    }
    private fun lukkKamera() {
        try {
            kameraLaas.acquire()
            startet = false
            if (omrade != null) {
                omrade!!.release()
                omrade = null
            }
            if (kameraSesjon != null) {
                kameraSesjon!!.close()
                kameraSesjon = null
            }
            if (kamera != null) {
                kamera!!.close()
                kamera = null
            }
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            kameraLaas.release()
        }
    }
    fun stopKamera() {
        lukkKamera()
        lukkBakgrunnstrad()
    }

    fun taBilde() {
        kameraEventListener.bildeTatt()
    }
    private fun apneBakgrunnstrad() {
        bakgrunnstrad = HandlerThread("camera_background_thread")
        bakgrunnstrad!!.start()
        bakgrunnshandler = Handler(bakgrunnstrad!!.looper)
    }

    private fun lukkBakgrunnstrad() {
        if(bakgrunnshandler == null)
            return

        bakgrunnstrad!!.quitSafely()
        bakgrunnstrad = null
        bakgrunnshandler = null
    }

    // Kode kopiert fra basic camera2 kotlin example

    /**
     * Configures the necessary [android.graphics.Matrix] transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     * @param viewWidth  The width of `mTextureView`
     * *
     * @param viewHeight The height of `mTextureView`
     */
    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
        if (null == forhandsvisningStorrelse) {
            return
        }
        val rotation = defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(0f, 0f, forhandsvisningStorrelse!!.height.toFloat(), forhandsvisningStorrelse!!.width.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale = Math.max(
                    viewHeight.toFloat() / forhandsvisningStorrelse!!.height,
                    viewWidth.toFloat() / forhandsvisningStorrelse!!.width)
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, centerX, centerY)
        }

        kameraEventListener.transformOppdatert(matrix)
    }
    private fun setupKamera(width: Int, height: Int)  {
        try {
            for (cameraId in kameraHandterer.cameraIdList) {
                val characteristics = kameraHandterer.getCameraCharacteristics(cameraId)

                // We don't use a front facing camera in this sample.
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue
                }

                val map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: continue

                // For still image captures, we use the largest available size.
                val largest = Collections.max(
                        Arrays.asList(*map.getOutputSizes(ImageFormat.JPEG)),
                        CompareSizesByArea())

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                val displayRotation = defaultDisplay.rotation
                val sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
                var swappedDimensions = false
                when (displayRotation) {
                    Surface.ROTATION_0, Surface.ROTATION_180 -> if (sensorOrientation == 90 || sensorOrientation == 270) {
                        swappedDimensions = true
                    }
                    Surface.ROTATION_90, Surface.ROTATION_270 -> if (sensorOrientation == 0 || sensorOrientation == 180) {
                        swappedDimensions = true
                    }
                    else -> Log.e(TAG, "Display rotation is invalid: $displayRotation")
                }

                val displaySize = Point()
                defaultDisplay.getSize(displaySize)
                var rotatedPreviewWidth = width
                var rotatedPreviewHeight = height
                var maxPreviewWidth = displaySize.x
                var maxPreviewHeight = displaySize.y

                if (swappedDimensions) {
                    rotatedPreviewWidth = height
                    rotatedPreviewHeight = width
                    maxPreviewWidth = displaySize.y
                    maxPreviewHeight = displaySize.x
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH
                }

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT
                }

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                forhandsvisningStorrelse = chooseOptimalSize(map.getOutputSizes<SurfaceTexture>(SurfaceTexture::class.java),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest)

                kameraEventListener.aspectOppdatert(forhandsvisningStorrelse!!)
                kameraId = cameraId
                return
            }
        } catch (e: CameraAccessException) {
            kameraEventListener.onFeil(e.message!!)
        } catch (e: NullPointerException) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            kameraEventListener.onFeil(e.message!!)
        }
    }

    companion object {

        /**
         * Conversion from screen rotation to JPEG orientation.
         */
        private val ORIENTATIONS = SparseIntArray()

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }

        /**
         * Tag for the [Log].
         */
        private const val TAG = "Bildr"

        /**
         * Max preview width that is guaranteed by Camera2 API
         */
        private const val MAX_PREVIEW_WIDTH = 1920

        /**
         * Max preview height that is guaranteed by Camera2 API
         */
        private const val MAX_PREVIEW_HEIGHT = 1080

        /**
         * Given `choices` of `Size`s supported by a camera, choose the smallest one that
         * is at least as large as the respective texture view size, and that is at most as large as the
         * respective max size, and whose aspect ratio matches with the specified value. If such size
         * doesn't exist, choose the largest one that is at most as large as the respective max size,
         * and whose aspect ratio matches with the specified value.
         * @param choices           The list of sizes that the camera supports for the intended output
         * *                          class
         * *
         * @param textureViewWidth  The width of the texture view relative to sensor coordinate
         * *
         * @param textureViewHeight The height of the texture view relative to sensor coordinate
         * *
         * @param maxWidth          The maximum width that can be chosen
         * *
         * @param maxHeight         The maximum height that can be chosen
         * *
         * @param aspectRatio       The aspect ratio
         * *
         * @return The optimal `Size`, or an arbitrary one if none were big enough
         */
        private fun chooseOptimalSize(choices: Array<Size>, textureViewWidth: Int,
                                      textureViewHeight: Int, maxWidth: Int, maxHeight: Int, aspectRatio: Size): Size {

            // Collect the supported resolutions that are at least as big as the preview Surface
            val bigEnough = ArrayList<Size>()
            // Collect the supported resolutions that are smaller than the preview Surface
            val notBigEnough = ArrayList<Size>()
            val w = aspectRatio.width
            val h = aspectRatio.height
            for (option in choices) {
                if (option.width <= maxWidth && option.height <= maxHeight && option.height == option.width * h / w) {
                    if (option.width >= textureViewWidth && option.height >= textureViewHeight) {
                        bigEnough.add(option)
                    } else {
                        notBigEnough.add(option)
                    }
                }
            }

            // Pick the smallest of those big enough. If there is no one big enough, pick the
            // largest of those not big enough.
            return when {
                bigEnough.size > 0 -> Collections.min(bigEnough, CompareSizesByArea())
                notBigEnough.size > 0 -> Collections.max(notBigEnough, CompareSizesByArea())
                else -> {
                    Log.e(TAG, "Couldn't find any suitable preview size")
                    choices[0]
                }
            }
        }

        /**
         * Compares two `Size`s based on their areas.
         */
        internal class CompareSizesByArea : Comparator<Size> {

            override fun compare(lhs: Size, rhs: Size): Int {
                // We cast here to ensure the multiplications won't overflow
                return java.lang.Long.signum(lhs.width.toLong() * lhs.height - rhs.width.toLong() * rhs.height)
            }

        }
    }
}