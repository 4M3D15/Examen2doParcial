package com.example.examen2ndoparcial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class TokenViewModel : ViewModel() {

    private val _timer = MutableLiveData<Int>()
    val timer: LiveData<Int> get() = _timer

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token

    val currentToken: String get() = _token.value ?: ""

    private var countdownJob: Job? = null

    fun generateToken() {
        _token.value = (100..999).random().toString()
        startCountdown()
    }

    private fun startCountdown() {
        countdownJob?.cancel() // Detener anterior si existe
        _timer.value = 15
        countdownJob = viewModelScope.launch {
            while (_timer.value!! > 0) {
                delay(1000)
                _timer.postValue(_timer.value!! - 1)
            }
            // Espera medio segundo y genera otro token
            delay(500)
            generateToken()
        }
    }

    fun saveAllData() {
        // Lógica para guardar selfie + datos + ubicación
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
