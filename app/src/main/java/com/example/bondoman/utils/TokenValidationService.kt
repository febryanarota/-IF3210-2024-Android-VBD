package com.example.bondoman.utils

import TokenManager
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.Runnable

class TokenValidationService : Service() {
    private lateinit var handler: Handler
    private lateinit var handlerThread: HandlerThread
    private lateinit var tokenCheckerRunnable: Runnable

    companion object {
        private const val INTERVAL = 5 * 1000L // default interval
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        handlerThread = HandlerThread("TokenValidationThread")
        handlerThread.start()

        handler = Handler(handlerThread.looper)

        tokenCheckerRunnable = object : Runnable {
            override fun run() {
                Log.i("TVALIDATION", "service starting")
                var remaining = TokenManager.getRemainingTime() * 1000L
                Log.i("TVALIDATION", "${remaining}")
                if (remaining <= 0) {
                    Log.e("TVALIDATION" , "expired")

                    // token expired, send the broadcast
                    val intent = Intent("com.example.bondoman.ACTION_TOKEN_EXPIRED")
                    sendBroadcast(intent)
                    stopSelf()
                }

                handler.postDelayed(this, remaining.toLong()) // change the interval into remaining time
            }
        }

        handler.postDelayed(tokenCheckerRunnable, INTERVAL)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        handlerThread.quitSafely()
    }
}