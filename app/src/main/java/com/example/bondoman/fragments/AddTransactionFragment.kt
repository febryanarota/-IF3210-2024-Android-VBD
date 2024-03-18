package com.example.bondoman.fragments

import android.os.Bundle
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
import java.lang.Long

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
        binding.autoCompleteText.setText("Pembelian", false)

        viewModel = ViewModelProvider(this, ViewModelFactory(
            TransactionRepository(
                TransactionDatabase.getDatabaseInstance(requireContext()))
        )
        ).get(TransactionViewModel::class.java)

        val args = this.arguments
        val idData = args?.getString("id", "")
        val titleData = args?.getString("title", "")
        val nominalData = args?.getString("nominal", "")
        val categoryData = args?.getString("category", "")
        val locationData = args?.getString("location", "")

        binding.transactionTitle.setText(titleData)
        binding.transactionNominal.setText(nominalData)
        binding.transactionCategory.setText(categoryData)
        binding.transactionLocation.setText(locationData)

        binding.bttnSave.setOnClickListener {
            val title = binding.transactionTitle.text.toString()
            val nominal = binding.transactionNominal.text.toString()
            val category = binding.transactionCategory.text.toString()
            val location = binding.transactionLocation.text.toString()
            if (idData != null) {
                val updatedTransaction = Transaction(id = Long.parseLong(idData), place = title, price = nominal, category = category, location = location)
                viewModel.updateTransaction(updatedTransaction)
            } else {
                val newTransaction = Transaction(place = title, price = nominal, category = category, location = location)
                viewModel.addTransaction(newTransaction)
            }
            findNavController().navigate(R.id.action_add_transaction_to_navigation_transaction)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}