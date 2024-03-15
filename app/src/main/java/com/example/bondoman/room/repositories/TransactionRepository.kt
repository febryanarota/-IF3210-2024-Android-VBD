package com.example.bondoman.repositories

import com.example.bondoman.room.database.TransactionDatabase
import com.example.bondoman.room.models.Transaction

class TransactionRepository(private val transactionDatabase: TransactionDatabase) {
    suspend fun insertTransaction(transaction: Transaction) {
        transactionDatabase.transactionDao().insertTransaction(transaction)
    }

    suspend fun insertTransactions(transaction: MutableList<Transaction>) {
        transactionDatabase.transactionDao().insertTransactions(transaction)
    }

    suspend fun deleteAll() {
        transactionDatabase.transactionDao().delete()
    }

    fun getAllTransactions() = transactionDatabase.transactionDao().getAllTransaction()
}