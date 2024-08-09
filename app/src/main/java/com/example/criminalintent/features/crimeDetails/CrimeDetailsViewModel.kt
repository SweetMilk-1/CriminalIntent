package com.example.criminalintent.features.crimeDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.criminalintent.database.CrimeRepository
import com.example.criminalintent.database.entities.Crime
import kotlinx.coroutines.launch
import java.util.UUID

/*
* ViewModel для CrimeDetails. Преимучество ViewMode - он не удаляется
* при удалении родительского компонента.
* Здесь можно легковесную хранить логику по работе с моделью приложения
* */
class CrimeDetailsViewModel:ViewModel() {
    private val crimeRepository = CrimeRepository.get()

    private val crimeIdLiveData: MutableLiveData<UUID> = MutableLiveData()
    val crimeLiveData : LiveData<Crime?> = crimeIdLiveData.switchMap {crimeId ->
        crimeRepository.getCrime(crimeId)
    }

    fun loadCrime(crimeId:UUID) {
        crimeIdLiveData.value = crimeId
    }

    fun updateCrime(crime: Crime) {
        viewModelScope.launch {
            crimeRepository.updateCrime(crime)
        }
    }
}