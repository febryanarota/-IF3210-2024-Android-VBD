package com.example.bondoman.utils

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import java.util.function.Consumer

object LocationUtils {
    private var _locationManager: LocationManager? = null
    private var finePermissionGranted: Boolean = false
    private fun getLocationManager(caller: Fragment): LocationManager? {
        if (_locationManager == null && finePermissionGranted) {
            _locationManager = caller.requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        return _locationManager
    }

    private fun checkPermission(caller: Fragment): Boolean {
        finePermissionGranted = PermissionUtils.checkPermissions(caller, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
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
            // TODO: check network / gps capability
            getLocationManager(caller)?.getCurrentLocation(
                LocationManager.PASSIVE_PROVIDER,
                null,
                caller.requireContext().mainExecutor,
                action
            )
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