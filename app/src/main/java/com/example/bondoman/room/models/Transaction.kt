package com.example.bondoman.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var place: String = "",
    var category: String = "Pembelian",
    var price: String = "",
    var date: Date = Date(),
    var location: String = "Ganyang",
)
