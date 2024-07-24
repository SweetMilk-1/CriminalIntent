package com.example.criminalintent.database

import android.content.Context
import java.lang.IllegalStateException
import java.util.UUID
import androidx.room.Room as Room1

private const val DATABASE_NAME = "crime-database"
class CrimeRepository private constructor(context: Context) {

    private val crimeDatabase : CrimeDatabase = Room1.databaseBuilder(
        context,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val crimeDao = crimeDatabase.crimeDao()

    fun getCrimeList() = crimeDao.getCrimeList()
    fun getCrime(id: UUID) = crimeDao.getCrime(id)
    companion object {
        private var crimeRepository: CrimeRepository? = null

        fun initialize(context: Context) {
            if (crimeRepository == null) {
                crimeRepository = CrimeRepository(context)
            }
        }

        fun get() =
            crimeRepository ?: throw IllegalStateException("CrimeRepository must be initialized")
    }
}