package com.example.criminalintent.database

import androidx.room.TypeConverter
import java.util.Date
import java.util.UUID

//Класс для конвертации типов данных БД в тип данных Kotlin
//В данном случае конвертируются Data и UUID
class CrimeTypeConverters {
    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? = millisSinceEpoch?.let {
        Date(it)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? = uuid?.toString()

    @TypeConverter
    fun toUUID(uuidString: String?) : UUID? = uuidString.let {
        UUID.fromString(it!!)
    }
}