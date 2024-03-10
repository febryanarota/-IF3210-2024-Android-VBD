package com.example.bondoman.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bondoman.R
import com.example.bondoman.adapter.TransactionAdapter
import com.example.bondoman.databinding.TransactionFragmentBinding
import com.example.bondoman.models.Transaction

class TransactionFragment: Fragment() {
    private var _binding: TransactionFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TransactionFragmentBinding.inflate(inflater, container, false)

        val transactions = createTransaction()
        binding.rvTransactions.adapter = TransactionAdapter(requireContext(), transactions)
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    private fun createTransaction(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        for (i in 1..150) transactions.add(Transaction("Transaction $i", "Pembelian $i", "IDR 15.000", "09/03/2024", "Ganyang"))
        return transactions
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//    }
//
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}