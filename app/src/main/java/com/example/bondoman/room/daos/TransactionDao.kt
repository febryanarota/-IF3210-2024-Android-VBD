package com.example.bondoman.room.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bondoman.room.models.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction) : Long // Long as the key that represents the id

    @Insert
    suspend fun insertTransactions(transaction: List<Transaction>) : List<Long>

    @Delete
    suspend fun removeTransaction(transaction: List<Transaction>) : Int

    @Query("DELETE FROM 'transaction'")
    suspend fun delete()

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateTransaction(transaction: List<Transaction>) : Int

    @Query("SELECT * FROM 'transaction' ORDER BY date DESC")
    fun getAllTransaction() : Flow<List<Transaction>>

}