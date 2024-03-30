package com.example.bondoman.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bondoman.R
import com.example.bondoman.databinding.ActivityMainBinding
import com.example.bondoman.fragments.AddTransactionFragment
import com.example.bondoman.fragments.SettingsFragment
import com.example.bondoman.utils.TokenValidationService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
//    private val random = AddTransactionFragment().getRandomizeReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//
//        val intent = IntentFilter(SettingsFragment.RANDOMIZE_ACTION)
//        registerReceiver(randomizeReceiver, intent, RECEIVER_NOT_EXPORTED)

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

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_transaction, R.id.navigation_scan, R.id.navigation_graph, R.id.navigation_settings
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

    private val randomizeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action === SettingsFragment.RANDOMIZE_ACTION) {
                Toast.makeText(context, "RECEIVED RANDOMIZE TRANSACTION", Toast.LENGTH_SHORT).show()
                val title = intent.getStringExtra("title")
                val nominal = intent.getStringExtra("nominal")
                val category = intent.getStringExtra("category")

                val bundle = Bundle().apply {
                    putString("title", title)
                    putString("nominal", nominal)
                    putString("category", category)
                }
//                val navController = findNavController(R.id.nav_host_fragment_activity_main)
//                navController.navigate(R.id.action_navigation_settings_to_add_transaction, bundle)
            }
        }
    }

    private fun navigateToLogin(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
//        unregisterReceiver(randomizeReceiver)
    }
}