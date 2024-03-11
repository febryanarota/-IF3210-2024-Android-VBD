package com.example.bondoman.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val place: String,
    val category: String,
    val price: String,
    val date: String,
    val location: String
)
