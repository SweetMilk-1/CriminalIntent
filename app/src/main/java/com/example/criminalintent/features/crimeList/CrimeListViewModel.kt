package com.example.criminalintent.features.crimeList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criminalintent.database.CrimeRepository
import com.example.criminalintent.database.entities.Crime
import kotlinx.coroutines.launch


class CrimeListViewModel: ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    val crimeList = crimeRepository.getCrimeList()

    fun addCrime(crime: Crime) {
        viewModelScope.launch {
            crimeRepository.addCrime(crime)
        }
    }
}