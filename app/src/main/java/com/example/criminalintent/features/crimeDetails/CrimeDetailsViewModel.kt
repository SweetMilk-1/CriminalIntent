package com.example.criminalintent.features.crimeDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.criminalintent.database.CrimeRepository
import com.example.criminalintent.database.entities.Crime
import java.util.UUID

class CrimeDetailsViewModel:ViewModel() {
    private val crimeRepository = CrimeRepository.get()

    private val crimeIdLiveData: MutableLiveData<UUID> = MutableLiveData()
    val crimeLiveData : LiveData<Crime?> = crimeIdLiveData.switchMap {crimeId ->
        crimeRepository.getCrime(crimeId)
    }

    fun loadCrime(crimeId:UUID) {
        crimeIdLiveData.value = crimeId
    }
}