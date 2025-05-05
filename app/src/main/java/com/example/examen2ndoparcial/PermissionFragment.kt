package com.example.examen2ndoparcial

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class PermissionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_permission, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnGrant = view.findViewById<Button>(R.id.btnGrant)
        btnGrant.setOnClickListener {
            val perms = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            // Si ya están concedidos, navega directamente
            if (perms.all {
                    ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
                }) {
                findNavController().navigate(R.id.toCamera)
            } else {
                // Pide permisos
                ActivityCompat.requestPermissions(requireActivity(), perms, 100)
            }
        }
    }

    // Tras la petición de permisos, si se conceden, navega a cámara
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            findNavController().navigate(R.id.toCamera)
        }
    }
}
