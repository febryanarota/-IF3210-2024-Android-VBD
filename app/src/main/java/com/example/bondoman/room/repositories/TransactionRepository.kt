package com.example.bondoman.repositories

import com.example.bondoman.room.database.TransactionDatabase
import com.example.bondoman.room.models.Transaction

class TransactionRepository(private val transactionDatabase: TransactionDatabase) {
    suspend fun insertTransaction(transaction: Transaction) {
        transactionDatabase.transactionDao().insertTransaction(transaction)
    }

    fun getAllTransactions() = transactionDatabase.transactionDao().getAllTransaction()
}