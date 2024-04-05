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

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDatabase.transactionDao().deleteTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDatabase.transactionDao().updateTransaction(transaction)
    }

    fun getAllTransactions() = transactionDatabase.transactionDao().getAllTransaction()

}