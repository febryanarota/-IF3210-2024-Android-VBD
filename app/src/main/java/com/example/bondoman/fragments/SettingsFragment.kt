package com.example.bondoman.fragments

import TokenManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.example.bondoman.databinding.BottomSheetLayoutBinding
import com.example.bondoman.databinding.FragmentSettingsBinding
import com.example.bondoman.repositories.TransactionRepository
import com.example.bondoman.room.database.TransactionDatabase
import com.example.bondoman.viewmodels.TransactionViewModel
import com.example.bondoman.viewmodels.ViewModelFactory

class SettingsFragment : Fragment() {

    private lateinit var viewModel: TransactionViewModel
    private lateinit var dialog: Dialog
    private lateinit var bottomSheetLayoutBinding: BottomSheetLayoutBinding

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

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

        bottomSheetLayoutBinding = BottomSheetLayoutBinding.inflate(inflater, container, false)

        // dialog for logout
        dialog = Dialog(requireContext())
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setContentView(bottomSheetLayoutBinding.root)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetLayoutBinding.bttnCancel.setBackgroundColor(Color.WHITE)
        bottomSheetLayoutBinding.textView2.text = "Are you sure want to log out from this account?"

        // logout
        binding.logoutButton.setOnClickListener {
            dialog.show()
            bottomSheetLayoutBinding.bttnDelete.text = "Logout"
            bottomSheetLayoutBinding.bttnDelete.setOnClickListener {
                TokenManager.removeToken()
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            bottomSheetLayoutBinding.bttnCancel.setOnClickListener {
                dialog.dismiss()
            }
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
            intent.putExtra("title", "Randomize Transaction")
            intent.putExtra("nominal", "1000000")
            intent.putExtra("category", "Pemasukan")
            requireActivity().sendBroadcast(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}