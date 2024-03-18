package com.example.bondoman.utils

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import okhttp3.internal.immutableListOf
import java.util.function.Consumer

object LocationUtils {
    private var _locationManager: LocationManager? = null
    private var finePermissionGranted: Boolean = false
    val PERMISSIONS_REQUIRED = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private fun getLocationManager(caller: Fragment): LocationManager? {
        if (_locationManager == null && finePermissionGranted) {
            _locationManager = caller.requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        return _locationManager
    }

    fun checkPermission(caller: Fragment): Boolean {
        finePermissionGranted = PermissionUtils.checkPermissions(caller, PERMISSIONS_REQUIRED)
        return finePermissionGranted
    }

    fun askPermission(caller: Fragment): Boolean {
        PermissionUtils.requirePermissions(caller, PERMISSIONS_REQUIRED) {}
        checkPermission(caller)
        return finePermissionGranted
    }

    fun getLocation(caller: Fragment, action: Consumer<Location>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getLocationROrAbove(caller, action)
        }
        else {
            getLocationBelowR(caller, action)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getLocationROrAbove(caller: Fragment, action: Consumer<Location>) {
        if (checkPermission(caller)) {
            when {
                getLocationManager(caller)?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true -> {
                    getLocationManager(caller)?.getCurrentLocation(
                        LocationManager.GPS_PROVIDER,
                        CancellationSignal(),
                        caller.requireContext().mainExecutor,
                        action
                    )
                }

                getLocationManager(caller)?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true -> {
                    getLocationManager(caller)?.getCurrentLocation(
                        LocationManager.NETWORK_PROVIDER,
                        CancellationSignal(),
                        caller.requireContext().mainExecutor,
                        action
                    )
                }
            }
        }
        else {
            caller.requireActivity().runOnUiThread {
                Toast.makeText(caller.requireContext(), "No Permission for location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLocationBelowR(caller: Fragment, action: Consumer<Location>) {
        if (checkPermission(caller)) {
            // TODO: check network / gps capability
            getLocationManager(caller)?.requestSingleUpdate(
                LocationManager.PASSIVE_PROVIDER,
                { location -> action.accept(location) },
                Looper.getMainLooper()
            )
        }
    }
}