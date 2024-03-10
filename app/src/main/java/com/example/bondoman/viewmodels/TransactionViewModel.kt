package com.example.bondoman.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bondoman.models.Transaction

private const val TAG = "TransactionViewModel"
class TransactionViewModel: ViewModel() {
    private val transactionsLiveData: MutableLiveData<MutableList<Transaction>>

    init {
        Log.i(TAG, "init")
        transactionsLiveData = MutableLiveData()
        transactionsLiveData.value = createTransaction()
    }

    fun getTransactions(): LiveData<MutableList<Transaction>> {
        return transactionsLiveData
    }
    private fun createTransaction(): MutableList<Transaction> {
        val transactions = mutableListOf<Transaction>()
        for (i in 1..150) transactions.add(Transaction("Transaction $i", "Pembelian $i", "IDR 15.000", "09/03/2024", "Ganyang"))
        return transactions
    }
}