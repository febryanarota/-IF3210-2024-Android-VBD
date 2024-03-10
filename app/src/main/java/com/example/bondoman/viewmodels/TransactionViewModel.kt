package com.example.bondoman.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bondoman.models.Transaction

private const val TAG = "TransactionViewModel"
class TransactionViewModel: ViewModel() {
    private val transactionsLiveData: MutableLiveData<MutableList<Transaction>>
    private val isRefreshingLiveData: MutableLiveData<Boolean>

    init {
        Log.i(TAG, "init")
        transactionsLiveData = MutableLiveData()
        transactionsLiveData.value = createTransaction()
        isRefreshingLiveData = MutableLiveData()
        isRefreshingLiveData.value = false
    }

    fun getTransactions(): LiveData<MutableList<Transaction>> {
        return transactionsLiveData
    }
    private fun createTransaction(): MutableList<Transaction> {
        val transactions = mutableListOf<Transaction>()
        for (i in 1..150) transactions.add(Transaction("Transaction $i", "Pembelian $i", "IDR 15.000", "09/03/2024", "Ganyang"))
        return transactions
    }

    fun getIsRefreshingData(): LiveData<Boolean> {
        return isRefreshingLiveData
    }
    fun fetchNewTransaction() {
        Log.i(TAG, "fetchNewTransaction")
        isRefreshingLiveData.value = true
        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                transactionsLiveData.value?.let { transactions ->
                    transactions.add(0, Transaction("Hiha", "Pembelian Hiha", "IDR 15.000", "09/03/2024", "Hiha"))
                    transactionsLiveData.value = transactions
                }
                isRefreshingLiveData.value = false
            }
        },1_000)
    }
}