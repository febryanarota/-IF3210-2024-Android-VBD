package com.example.bondoman.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = -1,
    val place: String = "",
    val category: String = "",
    val price: String = "",
    val date: Date = Date(),
    val location: String = "",
)
