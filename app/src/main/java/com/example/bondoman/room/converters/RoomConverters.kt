package com.example.bondoman.room.converters

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

class RoomConverters {
    @TypeConverter
    fun convertDateToLong(date: Date) : Long {
        return date.time
    }

    @TypeConverter
    fun convertLongToDate(timeLong: Long) : Date {
        return Date(timeLong)
    }
}