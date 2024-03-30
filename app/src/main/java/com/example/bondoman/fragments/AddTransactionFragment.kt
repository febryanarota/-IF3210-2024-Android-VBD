package com.example.bondoman.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bondoman.R
import com.example.bondoman.databinding.FragmetAddTransactionBinding
import com.example.bondoman.repositories.TransactionRepository
import com.example.bondoman.room.database.TransactionDatabase
import com.example.bondoman.room.models.Transaction
import com.example.bondoman.viewmodels.TransactionViewModel
import com.example.bondoman.viewmodels.ViewModelFactory

class AddTransactionFragment() : Fragment() {
    private lateinit var viewModel: TransactionViewModel

    private var _binding: FragmetAddTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmetAddTransactionBinding.inflate(inflater, container, false)

        val items = listOf<String>("Pembelian", "Pemasukan")
        val adapterItems = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)

        binding.autoCompleteText.setAdapter(adapterItems)

        viewModel = ViewModelProvider(this, ViewModelFactory(
            TransactionRepository(
                TransactionDatabase.getDatabaseInstance(requireContext()))
        )
        ).get(TransactionViewModel::class.java)

        val args = this.arguments
        val idData = args?.getString("id", "")
        val titleData = args?.getString("title", "")
        val nominalData = args?.getString("nominal", "")
        val categoryData = args?.getString("category", "Pembelian")
        val locationData = args?.getString("location", "")

        binding.transactionTitle.setText(titleData)
        binding.transactionNominal.setText(nominalData)
        if (categoryData != null) {
            binding.autoCompleteText.setText(categoryData, false)
        } else {
            binding.autoCompleteText.setText("Pembelian", false)
        }
        binding.transactionLocation.setText(locationData)

        binding.bttnSave.setOnClickListener {
            val title = binding.transactionTitle.text.toString()
            val nominal = binding.transactionNominal.text.toString()
            val category = binding.autoCompleteText.text.toString()
            val location = binding.transactionLocation.text.toString()
            if (isDataValid(title, nominal, category, location)) {
                if (idData != null) {
                    val updatedTransaction = Transaction(id = idData.toLong(), place = title, price = nominal, category = category, location = location)
                    viewModel.updateTransaction(updatedTransaction)
                    Log.i("UPDATE TRANSACTION", "Transaction updated!")
                } else {
                    val newTransaction = Transaction(place = title, price = nominal, category = category, location = location)
                    viewModel.addTransaction(newTransaction)
                    Log.i("ADD TRANSACTION", "Transaction added!")
                }
                findNavController().navigate(R.id.action_add_transaction_to_navigation_transaction)
            } else {
                binding.errorMessage.text = "Fields cannot be empty!"
            }
        }
        return binding.root
    }

    private fun isDataValid(title: String, nominal: String, category: String, location: String): Boolean {
        return if (title.isNotBlank() && nominal.isNotBlank() && category.isNotBlank() && location.isNotBlank()) {
            true
        } else {
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}