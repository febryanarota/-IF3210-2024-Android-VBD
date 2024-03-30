package com.example.bondoman.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import com.example.bondoman.databinding.BottomSheetLayoutBinding
import com.example.bondoman.databinding.FragmentTransactionBinding
import com.example.bondoman.repositories.TransactionRepository
import com.example.bondoman.room.database.TransactionDatabase
import com.example.bondoman.room.models.Transaction
import com.example.bondoman.viewmodels.TransactionViewModel
import com.example.bondoman.viewmodels.ViewModelFactory


private const val TAG = "TransactionFragment"
class TransactionFragment : Fragment(), TransactionAdapter.TransactionClickListener {
    private lateinit var viewModel: TransactionViewModel
    private lateinit var dialog: Dialog
    private lateinit var bottomSheetLayoutBinding: BottomSheetLayoutBinding

    private var _binding: FragmentTransactionBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        bottomSheetLayoutBinding = BottomSheetLayoutBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this, ViewModelFactory(
            TransactionRepository(
                TransactionDatabase.getDatabaseInstance(requireContext()))
        )).get(TransactionViewModel::class.java)

        dialog = Dialog(requireContext())

        val transactions = mutableListOf<Transaction>()
        val transactionAdapter = TransactionAdapter(requireContext(), transactions, viewModel, this)
        binding.rvTransactions.adapter = transactionAdapter
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())

        viewModel.getAllTransaction().observe(viewLifecycleOwner, Observer {transactionSnapshot ->
            if (transactionSnapshot != null && transactionSnapshot.isNotEmpty()) {
                transactions.clear()
                transactions.addAll(transactionSnapshot)
                transactionAdapter.notifyDataSetChanged()
            } else {
                for (i in 1..5) {
                    viewModel.addTransaction(Transaction(place = "Warteg", category = "Pembelian", location = "Ganyang", price = "15000"))
                }
            }
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

        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setContentView(bottomSheetLayoutBinding.root)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetLayoutBinding.bttnCancel.setBackgroundColor(Color.TRANSPARENT)


        return binding.root
    }

    override fun onEditTransaction(transaction: Transaction) {
        val bundle = Bundle().apply {
            putString("id", transaction.id.toString())
            putString("title", transaction.place)
            putString("nominal", transaction.price)
            putString("category", transaction.category)
            putString("location", transaction.location)
        }

        findNavController().navigate(R.id.action_navigation_transaction_to_add_transaction, bundle)
    }

    override fun onLocationClicked(transaction: Transaction) {
        // temp
        val mapURI = Uri.parse("https://maps.google.com/maps/search/${transaction.location}")
        val intent = Intent(Intent.ACTION_VIEW, mapURI)
        startActivity(intent)
    }

    override fun onDeleteClicked(transaction: Transaction) {
        dialog.show()
        bottomSheetLayoutBinding.bttnDelete.setOnClickListener {
            viewModel.deleteTransaction(transaction)
            dialog.dismiss()
        }
        bottomSheetLayoutBinding.bttnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}