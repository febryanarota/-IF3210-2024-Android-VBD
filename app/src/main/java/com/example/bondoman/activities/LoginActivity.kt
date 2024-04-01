package com.example.bondoman.activities

import TokenManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.bondoman.R
import com.example.bondoman.databinding.ActivityLoginBinding
import com.example.bondoman.receivers.NetworkReceiver
import com.example.bondoman.utils.TokenValidationService
import com.example.bondoman.viewmodels.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var networkReceiver: NetworkReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // Observe token result
        loginViewModel.token.observe(this) { token ->
            if (token.isNotEmpty()) {
                TokenManager.init(this)
                TokenManager.saveToken(token)
                restartTokenValidationService()
                val sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val email = binding.emailEditText.text.toString()
                editor.putString("email", email)
                editor.apply()
                navigateToMain(this)
            }
        }

        loginViewModel.message.observe(this) { message ->
            binding.message.text = message
        }

        // login button click listener
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            loginViewModel.login(email, password)
        }

        loginViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loginButton.setEnabled(false)
                binding.loginButton.setText(R.string.loading_login)
            } else {
                binding.loginButton.setEnabled(true)
                binding.loginButton.setText(R.string.login_button)
            }
        }

        supportActionBar?.hide()
        // If no internet, navigate to main
        networkReceiver = object: NetworkReceiver(this@LoginActivity) {
            override fun onNetworkChange(state: Companion.NetworkState) {
                when (state) {
                    Companion.NetworkState.NOT_CONNECTED -> {
                        Log.e("CONN_LOGIN", "NOT CONNECTED")
                        lifecycleScope.launch (Dispatchers.Main) {
                            navigateToMain(this@LoginActivity)
                        }
                    }
                    Companion.NetworkState.METERED -> {

                    }
                    Companion.NetworkState.NOT_METERED -> {

                    }
                }
            }
        }

    }
    private fun navigateToMain(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        finish()
    }

    private fun restartTokenValidationService() {
        val serviceIntent = Intent(this, TokenValidationService::class.java)
        stopService(serviceIntent)
        startService(serviceIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        networkReceiver.disconnect()
    }
}