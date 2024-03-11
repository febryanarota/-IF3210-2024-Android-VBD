package com.example.bondoman.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bondoman.R
import com.example.bondoman.databinding.ActivityLoginBinding
import com.example.bondoman.viewmodels.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // Observe login result
        loginViewModel.loginResult.observe(this, Observer { loginResult ->
            if (loginResult) {
                // Navigate to MainActivity
                navigateToMain(this)
            } else {
                // Handle login failure
            }
        })

        loginViewModel.message.observe(this, Observer {message ->
            binding.message.text = message
        })

        // Set up login button click listener
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            // Call login method in the ViewModel
            loginViewModel.login(email, password)
        }
    }
        private fun navigateToMain(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            // Note: We don't finish the activity here since we're not in the activity scope
        }
}