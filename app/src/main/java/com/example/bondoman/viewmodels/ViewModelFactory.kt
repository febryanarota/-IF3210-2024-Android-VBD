package com.example.bondoman.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bondoman.repositories.TransactionRepository
import com.example.bondoman.room.models.Transaction

class ViewModelFactory(private val transactionRepository: TransactionRepository)
    : ViewModelProvider.NewInstanceFactory() {

        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom((TransactionViewModel::class.java))) {
                return TransactionViewModel(transactionRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel Class")
        }
}