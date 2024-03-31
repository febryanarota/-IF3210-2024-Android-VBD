package com.example.bondoman.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bondoman.R
import com.example.bondoman.activities.LoginActivity
import com.example.bondoman.databinding.FragmentSettingsBinding
import com.example.bondoman.repositories.TransactionRepository
import com.example.bondoman.room.database.TransactionDatabase
import com.example.bondoman.viewmodels.TransactionViewModel
import com.example.bondoman.viewmodels.ViewModelFactory

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TransactionViewModel

    companion object {
        const val RANDOMIZE_ACTION = "com.example.bondoman.RANDOMIZE_TRANSACTIONS"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this, ViewModelFactory(
            TransactionRepository(
                TransactionDatabase.getDatabaseInstance(requireContext()))
        )).get(TransactionViewModel::class.java)

        binding.logoutButton.setOnClickListener {
            TokenManager.removeToken()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()  // finish the current activity (MainActivity)
        }
        val sendButton = binding.sendButton
        sendButton.setOnClickListener {
            val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("email", "") // Default value is an empty string
            viewModel.sendEmail(requireContext(), userEmail!!)
        }

        val downloadButton = binding.downloadButton
        downloadButton.setOnClickListener {
            Toast.makeText(context, "Downloading file..", Toast.LENGTH_LONG).show()
            viewModel.downloadTransaction()
        }

        val randomizeTransactionButton = binding.randomizeButton
        randomizeTransactionButton.setOnClickListener {
            val intent = Intent(RANDOMIZE_ACTION)
            val id = (1..100).random()
            val nominal = (1000..1000000).random().toString()
            val category = if ((0..1).random() == 0) "Pemasukan" else "Pembelian"
            intent.putExtra("title", "Randomize Transaction ${id}")
            intent.putExtra("nominal", nominal)
            intent.putExtra("category", category)
            requireActivity().sendBroadcast(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}