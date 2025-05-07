package com.example.examen2ndoparcial

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private val vm: CameraViewModel by viewModels()
    private lateinit var previewView: PreviewView
    private lateinit var imageCaptured: ImageView
    private lateinit var btnShoot: FloatingActionButton
    private lateinit var btnSave: Button
    private lateinit var btnRetry: Button
    private lateinit var actionButtons: LinearLayout
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService

    private val requestCameraLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera()
        else findNavController().popBackStack()
    }

    private val requestWriteLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            vm.photo.value?.let { bmp ->
                actuallySave(bmp)
                findNavController().navigate(R.id.toToken)
            }
        } else {
            Toast.makeText(requireContext(), "Permiso denegado, no se puede guardar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_camera, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        previewView = view.findViewById(R.id.previewView)
        imageCaptured = view.findViewById(R.id.imageCaptured)
        btnShoot = view.findViewById(R.id.btnShoot)
        btnSave = view.findViewById(R.id.btnSave)
        btnRetry = view.findViewById(R.id.btnRetry)
        actionButtons = view.findViewById(R.id.actionButtons)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraLauncher.launch(Manifest.permission.CAMERA)
        } else {
            startCamera()
        }

        btnShoot.setOnClickListener { takePhoto() }
        btnSave.setOnClickListener { savePhotoAndNavigate() }
        btnRetry.setOnClickListener { retryPhoto() }
    }

    private fun startCamera() {
        ProcessCameraProvider.getInstance(requireContext()).also { future ->
            future.addListener({
                val provider = future.get()
                val preview = Preview.Builder()
                    .build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }
                imageCapture = ImageCapture.Builder()
                    .setTargetRotation(previewView.display.rotation)
                    .build()
                provider.unbindAll()
                provider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            }, ContextCompat.getMainExecutor(requireContext()))
        }
    }

    private fun takePhoto() {
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bmp = toBitmap(image)
                    image.close()
                    requireActivity().runOnUiThread {
                        imageCaptured.visibility = View.VISIBLE
                        imageCaptured.setImageBitmap(bmp)
                        actionButtons.visibility = View.VISIBLE
                        btnShoot.visibility = View.GONE
                        vm.onPhotoTaken(bmp)
                    }
                }
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraFragment", "Error: ${exc.message}", exc)
                }
            }
        )
    }

    private fun savePhotoAndNavigate() {
        vm.photo.value?.let { bmp ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    requestWriteLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else {
                    actuallySave(bmp)
                    findNavController().navigate(R.id.toToken)
                }
            } else {
                actuallySave(bmp)
                findNavController().navigate(R.id.toToken)
            }
        }
    }

    private fun actuallySave(bmp: Bitmap) {
        // Nombre fijo para todas las fotos:
        val filename = "examen2ndo parcial.jpg"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = requireContext().contentResolver
            val cv = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/CameraApp")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
            uri?.let {
                resolver.openOutputStream(it)?.use { out ->
                    bmp.compress(Bitmap.CompressFormat.JPEG, 95, out)
                }
                cv.clear()
                cv.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(it, cv, null, null)
            }
        } else {
            val uriString = MediaStore.Images.Media.insertImage(
                requireContext().contentResolver,
                bmp, filename, null
            )
            uriString?.let {
                val uri = Uri.parse(it)
                MediaScannerConnection.scanFile(
                    requireContext(),
                    arrayOf(uri.path),
                    arrayOf("image/jpeg"),
                    null
                )
            }
        }
    }

    private fun retryPhoto() {
        imageCaptured.setImageDrawable(null)
        imageCaptured.visibility = View.GONE
        actionButtons.visibility = View.GONE
        btnShoot.visibility = View.VISIBLE
        startCamera()
    }

    private fun toBitmap(image: ImageProxy): Bitmap {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining()).also { buffer.get(it) }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }
}
