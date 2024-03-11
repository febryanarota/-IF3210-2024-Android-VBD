package com.example.bondoman.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceControl.Transaction
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bondoman.R
import com.example.bondoman.databinding.ActivityMainBinding
import com.example.bondoman.fragments.SettingsFragment
import com.example.bondoman.fragments.TransactionFragment
import com.example.bondoman.viewmodels.TransactionViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // if !token
//        navigateToLogin(this)

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
    //
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
    }

    private fun navigateToLogin(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        // Note: We don't finish the activity here since we're not in the activity scope
    }

}
