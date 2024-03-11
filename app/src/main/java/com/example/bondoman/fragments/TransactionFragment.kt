package com.example.bondoman.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bondoman.adapter.TransactionAdapter
import com.example.bondoman.databinding.TransactionFragmentBinding
import com.example.bondoman.repositories.TransactionRepository
import com.example.bondoman.room.database.TransactionDatabase
import com.example.bondoman.room.models.Transaction
import com.example.bondoman.viewmodels.TransactionViewModel
import com.example.bondoman.viewmodels.ViewModelFactory

private const val TAG = "TransactionFragment"
class TransactionFragment: Fragment() {
    private lateinit var viewModel: TransactionViewModel
    private var _binding: TransactionFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TransactionFragmentBinding.inflate(inflater, container, false)

        val transactions = mutableListOf<Transaction>()
        val transactionAdapter = TransactionAdapter(requireContext(), transactions)
        binding.rvTransactions.adapter = transactionAdapter
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())

//        viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        viewModel = ViewModelProvider(this, ViewModelFactory(TransactionRepository(TransactionDatabase.getDatabaseInstance(requireContext())))).get(TransactionViewModel::class.java)

        viewModel.getAllTransaction().observe(viewLifecycleOwner, Observer {transactionSnapshot ->
            Log.i(TAG, "Received transactions from view model")
            transactions.clear()
            transactions.addAll(transactionSnapshot)
            transactionAdapter.notifyDataSetChanged()
        })
//        viewModel.getIsRefreshingData().observe(viewLifecycleOwner, Observer {isRefreshing ->
//            binding.swipeContainer.isRefreshing = isRefreshing
//        })
//
//        binding.swipeContainer.setOnRefreshListener {
//            viewModel.fetchNewTransaction()
//        }

        return binding.root
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