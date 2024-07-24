package com.example.criminalintent.features.crimeList
import androidx.lifecycle.ViewModel
import com.example.criminalintent.database.CrimeRepository
import com.example.criminalintent.database.entities.Crime


class CrimeListViewModel: ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    val crimeList = crimeRepository.getCrimeList()
}