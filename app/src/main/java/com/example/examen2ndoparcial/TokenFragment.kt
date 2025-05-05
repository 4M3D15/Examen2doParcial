package com.example.examen2ndoparcial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

class TokenFragment : Fragment() {

    private val vm: TokenViewModel by viewModels()
    private lateinit var txtTimer: TextView
    private lateinit var txtGeneratedToken: TextView
    private lateinit var editToken: EditText
    private lateinit var btnAccept: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_token, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        txtTimer = view.findViewById(R.id.txtTimer)
        txtGeneratedToken = view.findViewById(R.id.txtGeneratedToken)
        editToken = view.findViewById(R.id.editToken)
        btnAccept = view.findViewById(R.id.btnAccept)

        btnAccept.isEnabled = false
        vm.generateToken()

        vm.token.observe(viewLifecycleOwner) { token ->
            txtGeneratedToken.text = "Token generado: $token"
            editToken.setText("")
            btnAccept.isEnabled = false
        }

        vm.timer.observe(viewLifecycleOwner) { secs ->
            txtTimer.text = "Tiempo restante: ${secs}s"
            btnAccept.isEnabled = editToken.text.toString() == vm.currentToken && secs > 0
        }

        editToken.doOnTextChanged { text, _, _, _ ->
            val isCorrect = text.toString() == vm.currentToken
            val isTimeValid = vm.timer.value ?: 0 > 0
            btnAccept.isEnabled = isCorrect && isTimeValid
        }

        btnAccept.setOnClickListener {
            vm.saveAllData()
            findNavController().navigate(R.id.toSuccess)
        }
    }
}
