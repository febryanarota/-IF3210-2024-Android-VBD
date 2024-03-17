package com.example.bondoman.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bondoman.R
import com.example.bondoman.adapter.TransactionAdapter
import com.example.bondoman.databinding.FragmentTransactionBinding
import com.example.bondoman.repositories.TransactionRepository
import com.example.bondoman.room.database.TransactionDatabase
import com.example.bondoman.room.models.Transaction
import com.example.bondoman.viewmodels.TransactionViewModel
import com.example.bondoman.viewmodels.ViewModelFactory


private const val TAG = "TransactionFragment"
class TransactionFragment : Fragment(), TransactionAdapter.TransactionClickListener {
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

        viewModel = ViewModelProvider(this, ViewModelFactory(
            TransactionRepository(
                TransactionDatabase.getDatabaseInstance(requireContext()))
        )).get(TransactionViewModel::class.java)

        val transactions = mutableListOf<Transaction>()
        val transactionAdapter = TransactionAdapter(requireContext(), transactions, viewModel, this)
        binding.rvTransactions.adapter = transactionAdapter
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        viewModel.deleteAll()

        viewModel.getAllTransaction().observe(viewLifecycleOwner, Observer {transactionSnapshot ->
            if (transactionSnapshot != null && transactionSnapshot.isNotEmpty()) {
                transactions.clear()
                transactions.addAll(transactionSnapshot)
            } else {
                for (i in 1..5) {
                    viewModel.addTransaction("Warteg")
                }
            }
            transactionAdapter.notifyDataSetChanged()
        })
        viewModel.getIsRefreshingData().observe(viewLifecycleOwner, Observer {isRefreshing ->
            binding.swipeContainer.isRefreshing = isRefreshing
        })

        binding.swipeContainer.setOnRefreshListener {
            viewModel.fetchNewTransaction()
        }

        binding.bttnAddTransaction.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_transaction_to_add_transaction)
        }

        return binding.root
    }

    override fun onEditTransaction(transaction: Transaction) {
        val bundle = Bundle().apply {
            putString("title", transaction.place)
            putString("nominal", transaction.price)
            putString("category", transaction.category)
            putString("location", transaction.location)
        }

        val fragment = AddTransactionFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_main, fragment).commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}