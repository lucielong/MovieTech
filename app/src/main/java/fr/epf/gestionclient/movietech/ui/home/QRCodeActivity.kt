package fr.epf.gestionclient.movietech.ui.home

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import fr.epf.gestionclient.movietech.MovieDetails
import fr.epf.gestionclient.movietech.R
import fr.epf.gestionclient.movietech.databinding.ActivityQrcodeBinding
import fr.epf.gestionclient.movietech.ui.details.MovieDetailsFragment
import java.io.File

class QRCodeActivity : AppCompatActivity() {


    private lateinit var binding: ActivityQrcodeBinding
    private var imageCapture: ImageCapture? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, Constants.REQUIRED_PERMISSIONS, Constants.REQUEST_CODE_PERMISSIONS)
        }
        binding.button.setOnClickListener {
            takePhoto()
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            externalMediaDirs.firstOrNull(),
            "${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(Constants.TAG, "Photo capture failed: ${exc.message}", exc)
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val image = InputImage.fromFilePath(this@QRCodeActivity, savedUri)
                    val scanner = BarcodeScanning.getClient()
                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            for (barcode in barcodes) {
                                val bounds = barcode.boundingBox
                                val corners = barcode.cornerPoints
                                val rawValue = barcode.rawValue
                                val valueType = barcode.valueType
                                // See API reference for complete list of supported types
                                when (valueType) {
                                    Barcode.TYPE_TEXT -> {
                                        val intValue = barcode.rawValue?.toIntOrNull()
                                        if (intValue != null) {
                                            Log.d(Constants.TAG, "onSuccess: $intValue")
                                            val bundle = bundleOf("movieId" to intValue)
                                            val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_details)
                                            Log.d(Constants.TAG, "navHostFragment: $navHostFragment")
                                            val navController = navHostFragment?.findNavController()
                                            Log.d(Constants.TAG, "navController: $navController")
                                        }
                                    }
                                    Barcode.TYPE_URL -> {
                                        val title = barcode.url!!.title
                                        val url = barcode.url!!.url
                                    }
                                }
                            }
                        }
                        .addOnFailureListener {
                            Log.d(Constants.TAG, "onFailure: ${it.message}")
                        }
                }
            })
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {mPreview ->
                mPreview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)
            } catch(e: Exception) {
                Log.d(Constants.TAG, "startCamera failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.REQUEST_CODE_PERMISSIONS) {
            if(allPermissionGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "We Don't Have Permission", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }



    private fun allPermissionGranted() = Constants.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
}
