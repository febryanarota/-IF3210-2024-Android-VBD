package com.example.bondoman.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bondoman.R
import com.example.bondoman.databinding.ActivityMainBinding
import com.example.bondoman.utils.LocationUtils
import com.example.bondoman.utils.TokenValidationService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val serviceIntent = Intent(this, TokenValidationService::class.java)
        startService(serviceIntent)
        registerReceiver(broadcastReceiver, IntentFilter("com.example.bondoman.ACTION_TOKEN_EXPIRED"), RECEIVER_EXPORTED)

        TokenManager.init(this)
        val token = TokenManager.getToken()
        if (token.isNullOrEmpty()) {
            navigateToLogin(this)
        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Start tracking location, if given permission
        LocationUtils.startTracking(this)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_transaction, R.id.navigation_scan, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action === "com.example.bondoman.ACTION_TOKEN_EXPIRED") {
                TokenManager.removeToken()
                navigateToLogin(this@MainActivity)
            }
        }
    }

    private fun navigateToLogin(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        finish()
    }
}