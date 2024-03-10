package com.example.bondoman.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceControl.Transaction
import androidx.fragment.app.Fragment
import com.example.bondoman.R
import com.example.bondoman.databinding.ActivityMainBinding
import com.example.bondoman.fragments.SettingsFragment
import com.example.bondoman.fragments.TransactionFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(TransactionFragment())

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
}
