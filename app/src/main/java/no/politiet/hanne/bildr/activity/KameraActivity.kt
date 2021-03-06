package no.politiet.hanne.bildr.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Size
import android.widget.FrameLayout
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.activity_kamera.*
import no.politiet.hanne.bildr.R
import no.politiet.hanne.bildr.dependencyinjection.repository
import no.politiet.hanne.bildr.kamera.KameraController
import no.politiet.hanne.bildr.kamera.KameraEventListener
import org.jetbrains.anko.toast
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val REQUEST_CAMERA_PERMISSION = 1888
class KameraActivity : AppCompatActivity(), KameraEventListener {

    private var kameraController : KameraController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kamera)
        btn_tabilde.setOnClickListener {kameraController!!.taBilde()}
        btn_visbilde.setOnClickListener { visBilder() }
        thumbnail.setOnClickListener { visBilder() }
        ActivityCompat.requestPermissions(this, arrayOf( Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        kameraController = KameraController(this.kamera_soker, this,this)
    }
    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            kameraController!!.restartKamera(kamera_soker)
            tellerOppdatert()
        }
    }
    override fun onStart() {
        super.onStart()
        layout_bildeteller.visibility = FrameLayout.GONE
    }
    override fun onStop() {
        super.onStop()
        kameraController!!.stopKamera()
    }
    override fun onFeil(message: Int) {
        onFeil(getString(message))
    }
    override fun onFeil(message: String) {
        Log.w(this.localClassName,message)
        toast(message)
    }
    override fun bildeTatt() {
        this@KameraActivity.runOnUiThread({
            val now : LocalDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")

            repository().bildeRepository.leggTilBilde("bilde_"+now.format(formatter),kamera_soker.bitmap)

            layout_bildevisning.visibility = RelativeLayout.VISIBLE
            kamera_soker.visibility = RelativeLayout.GONE
            bilde.setImageBitmap(kamera_soker.bitmap)
            tellerOppdatert()
            Handler(Looper.getMainLooper()).postDelayed({
                layout_bildevisning.visibility = RelativeLayout.GONE
                kamera_soker.visibility = RelativeLayout.VISIBLE
            }, 2000)
        })
    }
    override fun aspectOppdatert(forhandsvisningStorrelse: Size) {
        this@KameraActivity.runOnUiThread({
            // We fit the aspect ratio of TextureView to the size of preview we picked.
            val orientation = this.resources.configuration.orientation
            when (orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> this.kamera_soker.setAspectRatio(
                        forhandsvisningStorrelse.width, forhandsvisningStorrelse.height)
                else -> this.kamera_soker!!.setAspectRatio( forhandsvisningStorrelse.height, forhandsvisningStorrelse.width)
            }
            kamera_soker.surfaceTexture.setDefaultBufferSize(forhandsvisningStorrelse.width,forhandsvisningStorrelse.height)
        })
    }
    override fun transformOppdatert(matrix: Matrix) {
        this@KameraActivity.runOnUiThread({
            kamera_soker!!.setTransform(matrix)
        })
    }
    override fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }
    private fun visBilder() {
        val intent = Intent(this, VisBilderActivity::class.java)
        startActivity(intent)
    }
    fun tellerOppdatert() {
        val antall = repository().bildeRepository.tellBilder()
        if(antall == 3 && !this.isActivityTransitionRunning) {
            visBilder()
            return
        }
        bildeteller.text = antall.toString()
        oppdaterThumbnails()
    }
    private fun oppdaterThumbnails() {
        if (repository().bildeRepository.tellBilder() == 0) {
            layout_bildeteller.visibility = FrameLayout.GONE
            thumbnail.visibility = RelativeLayout.GONE
            thumbnail.setImageBitmap(null)
        } else {
            layout_bildeteller.visibility = FrameLayout.VISIBLE
            thumbnail.visibility = RelativeLayout.VISIBLE
            thumbnail.setImageBitmap(repository().bildeRepository.hentSisteBilde())
        }
    }
}

