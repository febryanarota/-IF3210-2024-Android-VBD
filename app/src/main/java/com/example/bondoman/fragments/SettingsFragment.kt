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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bondoman.R
import com.example.bondoman.activities.LoginActivity
import com.example.bondoman.databinding.BottomSheetLayoutBinding
import com.example.bondoman.databinding.DialogLayoutBinding
import com.example.bondoman.databinding.FragmentSettingsBinding
import com.example.bondoman.repositories.TransactionRepository
import com.example.bondoman.room.database.TransactionDatabase
import com.example.bondoman.viewmodels.TransactionViewModel
import com.example.bondoman.viewmodels.ViewModelFactory

class SettingsFragment : Fragment() {

    private lateinit var viewModel: TransactionViewModel
    private lateinit var dialog: Dialog
    private lateinit var dialogBinding: DialogLayoutBinding

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

        dialogBinding = DialogLayoutBinding.inflate(inflater, container, false)

        // dialog for logout
        dialog = Dialog(requireContext())
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        // logout
        binding.logoutButton.setOnClickListener {
            setLogoutDialog()
            dialogBinding.rightBtn.setOnClickListener {
                TokenManager.removeToken()
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            dialogBinding.leftBtn.setOnClickListener {
                dialog.dismiss()
            }
        }

        val sendButton = binding.sendButton
        sendButton.setOnClickListener {

            val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("email", "") // Default value is an empty string
            viewModel.isSendEmail.value = true
            viewModel.sendEmail(requireContext(), userEmail!!)
        }

        val downloadButton = binding.downloadButton
        downloadButton.setOnClickListener {
            setDownloadDialog()
            dialogBinding.leftBtn.setOnClickListener {
                downloadHandler("xls")
            }

            dialogBinding.rightBtn.setOnClickListener {
                downloadHandler("xlsx")
            }

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
            intent.setPackage(requireContext().packageName)
            requireActivity().sendBroadcast(intent)
        }

        return binding.root
    }

    private fun setLogoutDialog() {
        dialogBinding.textView.text = "Are you sure want to log out from this account?"
        dialogBinding.rightBtn.text = "Logout"
        dialogBinding.leftBtn.setBackgroundColor(Color.WHITE)
        dialogBinding.leftBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.P1))
        dialogBinding.leftBtn.text = "Cancel"
        dialog.show()
    }

    private fun setDownloadDialog() {
        dialogBinding.textView.text = "Choose the file format"
        dialogBinding.leftBtn.text = "xls"
        dialogBinding.rightBtn.text = "xlsx"
        dialogBinding.leftBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.P1))
        dialogBinding.leftBtn.setTextColor(Color.WHITE)
        dialog.show()
    }

    private fun downloadHandler(format: String) {
        Toast.makeText(context, "Downloading file..", Toast.LENGTH_LONG).show()
        viewModel.fileFormat.value = format
        viewModel.downloadTransaction()
        Toast.makeText(context, "File downloaded", Toast.LENGTH_LONG).show()
        dialog.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}