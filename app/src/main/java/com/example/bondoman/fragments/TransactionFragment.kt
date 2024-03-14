package com.example.bondoman.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bondoman.adapter.TransactionAdapter
import com.example.bondoman.databinding.FragmentTransactionBinding
import com.example.bondoman.repositories.TransactionRepository
import com.example.bondoman.room.database.TransactionDatabase
import com.example.bondoman.room.models.Transaction
import com.example.bondoman.viewmodels.TransactionViewModel
import com.example.bondoman.viewmodels.ViewModelFactory


private const val TAG = "TransactionFragment"
class TransactionFragment : Fragment() {
    private lateinit var viewModel: TransactionViewModel

    private var _binding: FragmentTransactionBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
//
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
//        return root


        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        val transactions = mutableListOf<Transaction>()
        val transactionAdapter = TransactionAdapter(requireContext(), transactions)
        binding.rvTransactions.adapter = transactionAdapter
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())

//        viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        viewModel = ViewModelProvider(this, ViewModelFactory(
            TransactionRepository(
                TransactionDatabase.getDatabaseInstance(requireContext()))
        )).get(TransactionViewModel::class.java)

        viewModel.deleteAll()
        for (i in 1..5) {
            viewModel.addTransaction("Warteg")
        }


        viewModel.getAllTransaction().observe(viewLifecycleOwner, Observer {transactionSnapshot ->
            Log.i(TAG, "Received transactions from view model")
            transactions.clear()
            transactions.addAll(transactionSnapshot)
            transactionAdapter.notifyDataSetChanged()
        })
        viewModel.getIsRefreshingData().observe(viewLifecycleOwner, Observer {isRefreshing ->
            binding.swipeContainer.isRefreshing = isRefreshing
        })

        binding.swipeContainer.setOnRefreshListener {
            viewModel.fetchNewTransaction()
        }


//
//        val bindingItem = ItemTransactionBinding.inflate(LayoutInflater.from(requireContext()), container, false)
//        bindingItem.bttnTrash.setOnClickListener {
//            viewModel.deleteAll()
//            Log.i("Transaction", "Clicked!!!")
//        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}