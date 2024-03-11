package com.example.bondoman.room.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bondoman.room.models.Transaction

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(transaction: Transaction) : Long // Long as the key that represents the id

    @Insert
    suspend fun insertTransactions(transaction: List<Transaction>) : List<Long>

    @Delete
    suspend fun removeTransaction(transaction: List<Transaction>) : Int

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateTransaction(transaction: List<Transaction>) : Int

    @Query("SELECT * FROM 'transaction'")
    suspend fun getAllTransaction() : List<Transaction>

}