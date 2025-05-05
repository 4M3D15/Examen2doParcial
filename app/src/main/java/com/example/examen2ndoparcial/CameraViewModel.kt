package com.example.examen2ndoparcial

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData

class CameraViewModel : ViewModel() {
    val photo = MutableLiveData<Bitmap>()
    fun onPhotoTaken(bitmap: Bitmap) {
        photo.value = bitmap
    }
}