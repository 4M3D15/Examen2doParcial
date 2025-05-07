package com.example.examen2ndoparcial

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class PermissionFragment : Fragment() {

    private lateinit var btnGrant: Button

    private val requestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.all { it }) {
            findNavController().navigate(R.id.toCamera)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_permission, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnGrant = view.findViewById(R.id.btnGrant)
        val perms = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (perms.all {
                ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
            }) {
            findNavController().navigate(R.id.toCamera)
        }

        btnGrant.setOnClickListener {
            requestLauncher.launch(perms)
        }
    }
}
