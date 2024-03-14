package com.example.bondoman.fragments

import TokenManager
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bondoman.activities.LoginActivity
import com.example.bondoman.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.logoutButton.setOnClickListener {
            TokenManager.removeToken()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()  // finish the current activity (MainActivity)
        }
        return binding.root
    }
}