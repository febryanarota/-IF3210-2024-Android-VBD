package com.example.bondoman.utils

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionUtils {
    companion object {
        fun requirePermissions(caller: Fragment, permissions: Array<String>, verbose: Boolean = false, operation: () -> Unit) {
            if (checkPermissions(caller, permissions)) {
                operation()
            } else {
                val requestPermissionLauncher =
                    caller.registerForActivityResult(RequestMultiplePermissions()) {
                            isGranted: Map<String, Boolean> ->
                        if (isGranted.values.reduce {acc, res -> acc && res}) {
                            operation()
                        } else {
                            if (verbose) {
                                Toast.makeText(caller.requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                requestPermissionLauncher.launch(permissions)
            }
        }

        fun checkPermissions(caller: Fragment, permissions: Array<String>): Boolean {
            return permissions
                .map { permission ->
                    ContextCompat.checkSelfPermission(caller.requireContext(), permission) == PackageManager.PERMISSION_GRANTED
                }
                .reduce {
                        cur, now -> cur && now
                }
        }
    }
}