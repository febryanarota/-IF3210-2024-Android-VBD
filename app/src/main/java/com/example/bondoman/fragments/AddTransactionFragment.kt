package com.example.bondoman.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bondoman.R
import com.example.bondoman.databinding.FragmetAddTransactionBinding
import com.example.bondoman.repositories.TransactionRepository
import com.example.bondoman.room.database.TransactionDatabase
import com.example.bondoman.room.models.Transaction
import com.example.bondoman.utils.LocationUtils
import com.example.bondoman.utils.TransactionFactory
import com.example.bondoman.viewmodels.TransactionViewModel
import com.example.bondoman.viewmodels.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

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
        val date = args?.getString("date", "")

        setUpArguments(idData, titleData, nominalData, categoryData, locationData)

        binding.bttnSave.setOnClickListener {
            onSaveButtonClicked(idData, date)
        }

        binding.bttnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.bttnSearchLoct.setOnClickListener {
            onSearchLocationButtonClicked()
        }

        return binding.root
    }

    private fun setUpArguments(idData: String?, titleData: String?, nominalData: String?, categoryData: String?, locationData: String?) {
        binding.transactionTitle.setText(titleData)
        if (nominalData != null) {
            binding.transactionNominal.setText(setPriceBack(nominalData).toString())
        } else {
            binding.transactionNominal.setText("0")
        }
        if (categoryData != null) {
            binding.autoCompleteText.setText(categoryData, false)
        } else {
            binding.autoCompleteText.setText("Pembelian", false)
        }
        if (idData == null || idData == "") {
            var location = ""
            lifecycleScope.launch(Dispatchers.Default) {
                val transactionFactory = TransactionFactory(this@AddTransactionFragment)
                try {
                    transactionFactory.setLocationAutomatic(this@AddTransactionFragment)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                transactionFactory.doWhenReady { transaction ->
                    location = transaction.location
                    launch(Dispatchers.Main) {
                        binding.transactionLocation.setText(location)
                    }
                }
            }
        } else {
            binding.transactionLocation.setText(locationData)
        }
    }

    private fun onSearchLocationButtonClicked() {
        lifecycleScope.launch(Dispatchers.Default) {
            val transactionFactory = TransactionFactory(this@AddTransactionFragment)
            try {
                transactionFactory.setLocationAutomatic(this@AddTransactionFragment)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            transactionFactory.doWhenReady { transaction ->
                Log.i("LOCATION", "transaction.location: ${transaction.location}")
                val location = transaction.location
                launch(Dispatchers.Main) {
                    binding.transactionLocation.setText(location)
                }
            }
        }
    }

    private fun onSaveButtonClicked(idData: String?, date: String?) {
        val format: NumberFormat = NumberFormat.getInstance(Locale("id", "ID"))
        Log.i("ADD TRANSACTION ON SAVE", "TES")

        if (!dataNotNUll()) {
            binding.errorMessage.text = "Fields cannot be empty!"
            return
        }

        val title = binding.transactionTitle.text.toString()
        val nominal = binding.transactionNominal.text.toString()
        val nominalFloat = format.parse(binding.transactionNominal.text.toString())?.toFloat() ?: 0F
        val category = binding.autoCompleteText.text.toString()
        val location = binding.transactionLocation.text.toString()

        if (isDataValid(title, nominal, category, location)) {
            if (idData != null && idData != "") {
                Log.i("UPDATE TRANSACTION", "TES")
                Log.i("UPDATE TRANSACTION", "ID: $idData")
                val updatedTransaction = Transaction(id = idData.toLong(), place = title, price = setPriceIDR(nominalFloat), category = category, location = location, date = Date(date))
                viewModel.updateTransaction(updatedTransaction)
            } else {
                Log.i("ADD TRANSACTION", "TES")
                val newTransaction = Transaction(place = title, price = setPriceIDR(nominalFloat), category = category, location = location)
                viewModel.addTransaction(newTransaction)
            }
            findNavController().navigate(R.id.action_add_transaction_to_navigation_transaction)
        } else {
            binding.errorMessage.text = "Fields cannot be empty!"
        }
    }

    private fun dataNotNUll(): Boolean {
        if (binding.transactionTitle.text.toString().isNotBlank() && binding.transactionNominal.text.toString().isNotBlank() && binding.autoCompleteText.text.toString().isNotBlank() && binding.transactionLocation.text.toString().isNotBlank()) {
            return true;
        } else {
            return false;
        }
    }

    private fun isDataValid(title: String, nominal: String, category: String, location: String): Boolean {
        return if (title.isNotBlank() && nominal.isNotBlank() && category.isNotBlank() && location.isNotBlank()) {
            true
        } else {
            false
        }
    }

    fun setPriceIDR(price: Float): String {
        val format = DecimalFormat.getNumberInstance(Locale("id", "ID"))
        format.apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
            isGroupingUsed = true
        }
        return "${format.format(price)}"
    }

    private fun setPriceBack(price: String?): Float {
        val format = DecimalFormat.getInstance(Locale("id", "ID"))
        format.apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
            isGroupingUsed = true
        }
        val price = format.parse(price)
        return price.toFloat()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}