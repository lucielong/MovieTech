package fr.epf.gestionclient.movietech.ui.home

import android.Manifest

object Constants {

    const val TAG = "cameraX"
    const val FILENAME_NAME_FORMAT = "yy-MM-dd-HH-mm-ss-SSS"
    const val REQUEST_CODE_PERMISSIONS = 123
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
}