package com.example.bondoman.activities

import TokenManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bondoman.databinding.ActivityLoginBinding
import com.example.bondoman.utils.TokenValidationService
import com.example.bondoman.viewmodels.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // Observe token result
        loginViewModel.token.observe(this, Observer { token ->
            if (token.isNotEmpty()) {
                TokenManager.init(this)
                TokenManager.saveToken(token)
                restartTokenValidationService()
                navigateToMain(this)
            }
        })

        loginViewModel.message.observe(this, Observer {message ->
            binding.message.text = message
        })

        // login button click listener
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            loginViewModel.login(email, password)
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
}