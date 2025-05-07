// RegisterFragment.kt
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

        val letterFilter = InputFilter { source, start, end, _, _, _ ->
            val pattern = Regex("[A-Z ]")
            val filtered = StringBuilder()
            for (i in start until end) {
                val c = source[i]
                if (pattern.matches(c.toString())) filtered.append(c)
            }
            filtered.toString()
        }
        editName.filters = arrayOf(AllCaps(), letterFilter)

        val alphaNumFilter = InputFilter { source, start, end, _, _, _ ->
            val pattern = Regex("[A-Z0-9]")
            val filtered = StringBuilder()
            for (i in start until end) {
                val c = source[i]
                if (pattern.matches(c.toString())) filtered.append(c)
            }
            filtered.toString()
        }
        editCurp.filters = arrayOf(LengthFilter(18), AllCaps(), alphaNumFilter)

        editName.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrBlank() || !text.all { it.isLetter() || it.isWhitespace() }) {
                errorName.text = "Nombre inválido"
                errorName.visibility = View.VISIBLE
            } else {
                errorName.visibility = View.GONE
            }
            validateForm()
        }

        editCurp.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrBlank() || text.length < 18 || !text.all { it.isLetterOrDigit() }) {
                errorCurp.text = "CURP inválido"
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
        val nameValid = editName.text?.isNotBlank() == true && editName.text!!.all { it.isLetter() || it.isWhitespace() }
        val curpValid = editCurp.text?.length == 18 && editCurp.text!!.all { it.isLetterOrDigit() }
        btnNext.isEnabled = nameValid && curpValid
    }
}
