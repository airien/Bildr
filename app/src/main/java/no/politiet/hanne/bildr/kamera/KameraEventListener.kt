package no.politiet.hanne.bildr.kamera

import android.graphics.Matrix
import android.util.Size

interface KameraEventListener {
    fun onFeil(message: Int)
    fun onFeil(message: String)
    fun bildeTatt()
    fun aspectOppdatert(forhandsvisningStorrelse: Size)
    fun transformOppdatert(matrix: Matrix)
    fun checkPermission(): Boolean
}
