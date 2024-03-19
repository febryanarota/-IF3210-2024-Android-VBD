package com.example.bondoman.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import okhttp3.internal.immutableListOf
import java.util.Locale
import java.util.function.Consumer

object LocationUtils: LocationListener {
    private var _locationManager: LocationManager? = null
    private var _geocoder: Geocoder? = null

    private var finePermissionGranted: Boolean = false
    private var _location: Location? = null
    private var _locationString: String? = null

    val location
        get() = _location!!
    val locationString
        get() = _locationString!!

    val PERMISSIONS_REQUIRED = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private const val minTime = 10000L           // 10 s
    private const val minDistance = 50f          // 50 m

    @SuppressLint("MissingPermission")
    fun startTracking(caller: AppCompatActivity) {
        PermissionUtils.requirePermissions(caller, PERMISSIONS_REQUIRED) {
            _locationManager = caller.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            _locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime,
                minDistance,
                this
            )
        }

        _geocoder = Geocoder(caller, Locale.getDefault())
    }

    fun stopTracking(caller: AppCompatActivity) {
        _locationManager?.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        _location = location
        _locationString = _geocoder?.getFromLocation(location.latitude, location.longitude, 1)?.get(0)?.getAddressLine(0)
    }

}