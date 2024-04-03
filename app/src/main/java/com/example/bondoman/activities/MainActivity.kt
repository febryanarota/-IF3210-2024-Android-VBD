package com.example.bondoman.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bondoman.R
import com.example.bondoman.databinding.ActivityMainBinding
import com.example.bondoman.utils.LocationUtils
import com.example.bondoman.fragments.SettingsFragment
import com.example.bondoman.receivers.NetworkReceiver
import com.example.bondoman.utils.TokenValidationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var networkReceiver: NetworkReceiver
    private var connected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = IntentFilter(SettingsFragment.RANDOMIZE_ACTION)
        registerReceiver(randomizeReceiver, intent, RECEIVER_NOT_EXPORTED)

        val serviceIntent = Intent(this, TokenValidationService::class.java)
        startService(serviceIntent)
        registerReceiver(broadcastReceiver, IntentFilter("com.example.bondoman.ACTION_TOKEN_EXPIRED"), RECEIVER_EXPORTED)

        TokenManager.init(this)
        val token = TokenManager.getToken()
//        THESE LINES MOVED INTO networkReceiver
//        if (token.isNullOrEmpty()) {
//            navigateToLogin(this)
//        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Start tracking location, if given permission
        LocationUtils.startTracking(this)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_transaction, R.id.navigation_twibbon, R.id.navigation_scan, R.id.navigation_graph, R.id.navigation_settings, R.id.add_transaction
            )
        )

        // Setup network receiver
        // Send toast message every time it detect changes
        networkReceiver = object: NetworkReceiver(this@MainActivity) {
            override fun onNetworkChange(state: Companion.NetworkState) {
                when (state) {
                    Companion.NetworkState.NOT_CONNECTED -> {
                        lifecycleScope.launch (Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "Not connected to internet", Toast.LENGTH_SHORT).show()
                        }
                        connected = false
                    }
                    Companion.NetworkState.METERED -> {
                        lifecycleScope.launch (Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "Connected with metered connection", Toast.LENGTH_SHORT).show()
                        }
                        connected = true
                    }
                    Companion.NetworkState.NOT_METERED -> {
                        lifecycleScope.launch (Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "Connected with non metered connection", Toast.LENGTH_SHORT).show()
                        }
                        connected = true
                    }
                }

                if (connected && token.isNullOrEmpty()) {
                    navigateToLogin(this@MainActivity)
                }
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action === "com.example.bondoman.ACTION_TOKEN_EXPIRED" && connected) {
                TokenManager.removeToken()
                navigateToLogin(this@MainActivity)
            }
        }
    }

    private val randomizeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action === SettingsFragment.RANDOMIZE_ACTION) {
//                Toast.makeText(context, "Successfully added new transaction", Toast.LENGTH_SHORT).show()
                val title = intent.getStringExtra("title")
                val nominal = intent.getStringExtra("nominal")
                val category = intent.getStringExtra("category")


                val bundle = Bundle().apply {
                    putString("title", title)
                    putString("nominal", nominal)
                    putString("category", category)
                }
                val navController = findNavController(R.id.nav_host_fragment_activity_main)
                navController.navigate(R.id.action_navigation_settings_to_add_transaction, bundle)
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
        unregisterReceiver(randomizeReceiver)
        unregisterReceiver(broadcastReceiver)
        networkReceiver.disconnect()
    }
}