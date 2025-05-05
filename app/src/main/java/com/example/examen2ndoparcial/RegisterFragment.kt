package com.example.examen2ndoparcial

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputFilter.AllCaps
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegisterFragment : Fragment() {

    private val vm: RegisterViewModel by viewModels()
    private lateinit var editName: TextInputEditText
    private lateinit var editCurp: TextInputEditText
    private lateinit var btnNext: MaterialButton
    private lateinit var errorName: TextView
    private lateinit var errorCurp: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_register, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        editName = view.findViewById(R.id.editName)
        editCurp = view.findViewById(R.id.editCurp)
        btnNext = view.findViewById(R.id.btnNext)
        errorName = view.findViewById(R.id.errorName)
        errorCurp = view.findViewById(R.id.errorCurp)

        // Forzar mayúsculas y limitar CURP a 18 caracteres
        editCurp.filters = arrayOf<InputFilter>(LengthFilter(18), AllCaps())

        // Validaciones en vivo
        editName.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrBlank() || text.any { it.isDigit() }) {
                errorName.text = "Nombre inválido (no debe tener números)"
                errorName.visibility = View.VISIBLE
            } else {
                errorName.visibility = View.GONE
            }
            validateForm()
        }

        editCurp.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrBlank() || text.length < 18) {
                errorCurp.text = "CURP incompleto"
                errorCurp.visibility = View.VISIBLE
            } else {
                errorCurp.visibility = View.GONE
            }
            validateForm()
        }

        btnNext.setOnClickListener {
            vm.validateCurp(editCurp.text.toString())
        }

        vm.curpState.observe(viewLifecycleOwner) { valid ->
            if (valid) {
                findNavController().navigate(R.id.toPermission)
            } else {
                errorCurp.text = "Información errónea"
                errorCurp.visibility = View.VISIBLE
            }
        }
    }

    private fun validateForm() {
        val nameValid = editName.text?.isNotBlank() == true && !editName.text.toString().any { it.isDigit() }
        val curpValid = editCurp.text?.length == 18
        btnNext.isEnabled = nameValid && curpValid
    }
}