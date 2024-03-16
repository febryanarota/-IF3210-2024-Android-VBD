package com.example.bondoman.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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
        val sendButton = binding.sendButton
        sendButton.setOnClickListener {
            sendEmail()
        }
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun sendEmail() {
        val mailIntent = Intent(Intent.ACTION_SEND)
        mailIntent.data = Uri.parse("mailto:")
        mailIntent.type = "text/plain"
        mailIntent.putExtra(Intent.EXTRA_EMAIL, "13521120@std.stei.itb.ac.id")
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, "test")
        mailIntent.putExtra(Intent.EXTRA_TEXT, "lorem ipsum")
        try {
            startActivity(Intent.createChooser(mailIntent, "Choose email intent..."))
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
        }
    }
}