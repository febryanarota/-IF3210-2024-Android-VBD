package com.example.bondoman.activities

import TokenManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.bondoman.R
import com.example.bondoman.databinding.ActivityMainBinding
import com.example.bondoman.fragments.SettingsFragment
import com.example.bondoman.fragments.TransactionFragment
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


        replaceFragment(SettingsFragment())

        binding.navbar.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.settings_nav -> {
                    replaceFragment(SettingsFragment())
                    true
                }
                R.id.transaction_nav -> {
                    replaceFragment(TransactionFragment())
                    true
                }
                else -> {
                    replaceFragment(SettingsFragment())
                    false
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action === "com.example.bondoman.ACTION_TOKEN_EXPIRED") {
                TokenManager.removeToken()
                navigateToLogin(this@MainActivity)
            }
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
    }

    private fun navigateToLogin(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        finish()
    }


}
