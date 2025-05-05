package com.example.examen2ndoparcial

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    private val _curpState = MutableLiveData<Boolean>()
    val curpState: LiveData<Boolean> get() = _curpState

    fun validateCurp(curp: String) {
        // Por ejemplo: validamos longitud 18
        _curpState.value = (curp.length == 18)
    }
}