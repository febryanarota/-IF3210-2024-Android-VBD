package com.example.bondoman.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.bondoman.room.converters.RoomConverters
import com.example.bondoman.room.daos.TransactionDao
import com.example.bondoman.room.models.Transaction

@Database(entities = [Transaction::class], version = 1, exportSchema = false) // false means it is not exporting the schema everytime there is version change
@TypeConverters(RoomConverters::class)
abstract class TransactionDatabase: RoomDatabase() {
    // abstract class so room can generate the class
    abstract fun transactionDao() : TransactionDao

    // companion object so it serves as static method
    companion object {
        private fun buildDatabase(context: Context) : TransactionDatabase {
            return Room.databaseBuilder(context, TransactionDatabase::class.java, "transaction.db").build()
        }

        @Volatile // so it is visible to other threads
        private var INSTANCE : TransactionDatabase? = null

        fun getDatabaseInstance(context: Context): TransactionDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }
    }
}