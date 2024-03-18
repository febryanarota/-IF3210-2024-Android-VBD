package com.example.bondoman.utils

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

// Create object only if request is not at initialization phase
// See companion object for simple usage
class PermissionUtils(
    caller: Fragment,
    private val operationSuccess: () -> Unit,
    private val operationFailed: () -> Unit
) {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    init {
        requestPermissionLauncher =
            caller.registerForActivityResult(RequestMultiplePermissions()) {
                    isGranted: Map<String, Boolean> ->
                if (isGranted.values.reduce {acc, res -> acc && res}) {
                    operationSuccess()
                } else {
                    operationFailed()
                }
            }
    }
    fun requirePermissions(caller: Fragment, permissions: Array<String>,) {
        if (checkPermissions(caller, permissions)) {
            operationSuccess()
        }
        else {
            requestPermissionLauncher.launch(permissions)
        }
    }
    companion object {
        // Hanya bisa dijalankan di initialization (onCreate, onStart, dll)
        // Selebihnya, buat objek di masa initialization & gunakan requirePermissions milik objek (bukan milik class)
        //
        // Overload fungsi jika diperlukan di Activity!
        // JANGAN UBAH FUNGSI KALAU MAU PAKAI COROUTINE!!! Launch waktu dipanggil saja.
        //
        // Contoh penggunaan:
        // requirePermission(this, arrayOf(Manifest.Permission.Camera)) {
        //     openCamera()
        // }
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