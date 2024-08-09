package com.example.criminalintent.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.criminalintent.database.entities.Crime
import java.util.UUID

//Объект для доступа к данным из БД
@Dao
abstract class CrimeDao {
    @Query("select * from crime")
    abstract fun getCrimeList() : LiveData<List<Crime>>

    @Query("select * from crime where id = :id")
    abstract fun getCrime(id: UUID) : LiveData<Crime?>

    @Insert
    abstract fun addCrime(crime: Crime)

    @Update
    abstract fun updateCrime(crime: Crime)
}