package com.example.bondoman.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.bondoman.repositories.TransactionRepository
import com.example.bondoman.room.models.Transaction
import kotlinx.coroutines.launch
import java.util.Date

private const val TAG = "TransactionViewModel"
class TransactionViewModel(private val transactionRepository: TransactionRepository): ViewModel() {
    fun getAllTransaction() = transactionRepository.getAllTransactions().asLiveData(viewModelScope.coroutineContext)

    fun addTransaction(place: String) = viewModelScope.launch {
        val transactionObject = Transaction(place = place)
        transactionRepository.insertTransaction(transactionObject)
    }
//    private val transactionsLiveData: MutableLiveData<List<Transaction>>
//    private val isRefreshingLiveData: MutableLiveData<Boolean>
//
//    init {
//        Log.i(TAG, "init")
//        transactionsLiveData = MutableLiveData()
//        transactionsLiveData.value = getAllTransaction()
//        isRefreshingLiveData = MutableLiveData()
//        isRefreshingLiveData.value = false
//    }
//
//
////    fun getTransactions(): LiveData<MutableList<Transaction>> {
////        return transactionsLiveData
////    }
////    private fun createTransaction(): MutableList<Transaction> {
////        val transactions = mutableListOf<Transaction>()
////        for (i in 1..150) transactions.add(Transaction(place = "Transaction $i", category = "Pembelian $i", price = "IDR 15.000", date = Date(), location = "Ganyang"))
////        return transactions
////    }
////
//    fun getIsRefreshingData(): LiveData<Boolean> {
//        return isRefreshingLiveData
//    }
//
//    fun fetchNewTransaction() {
//        Log.i(TAG, "fetchNewTransaction")
//        isRefreshingLiveData.value = true
//        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
//            override fun run() {
//                transactionsLiveData.value = getAllTransaction()
//                isRefreshingLiveData.value = false
//            }
//        },1_000)
//    }
}