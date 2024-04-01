package com.example.bondoman.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

abstract class NetworkReceiver(context: Context) {
    private var isConnectedNonMetered: Boolean = false
    private var isConnectedMetered: Boolean = false

    init {
        val conn = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val notMeteredNetworkRequest = NetworkRequest.Builder().apply {
            addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        }.build()
        val meteredNetworkRequest = NetworkRequest.Builder().apply {
            addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        }.build()

        // register callback for non metered connection
        conn.registerNetworkCallback(notMeteredNetworkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (!isConnectedNonMetered) {
                    isConnectedNonMetered = true
                    onNetworkChange(NetworkState.NOT_METERED)
                }
            }

            override fun onLost(network: Network) {
                isConnectedNonMetered = false
                if (isConnectedMetered) {
                    onNetworkChange(NetworkState.METERED)
                }
                else {
                    onNetworkChange(NetworkState.NOT_CONNECTED)
                }
            }
        })

        // register callback for non metered connection
        conn.registerNetworkCallback(meteredNetworkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (!isConnectedMetered && !isConnectedNonMetered) {
                    onNetworkChange(NetworkState.METERED)
                }
                isConnectedMetered = true
            }

            override fun onLost(network: Network) {
                onNetworkChange(NetworkState.NOT_CONNECTED)
                isConnectedMetered = false
            }
        })

    }



    public abstract fun onNetworkChange(state: NetworkState);

    companion object {
        enum class NetworkState {
            NOT_CONNECTED,
            METERED,
            NOT_METERED
        }
    }
}