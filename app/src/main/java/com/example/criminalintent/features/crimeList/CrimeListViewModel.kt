package com.example.criminalintent.features.crimeList
import androidx.lifecycle.ViewModel
import com.example.criminalintent.model.Crime

class CrimeListViewModel: ViewModel() {
    private var _crimeList: MutableList<Crime> = emptyList<Crime>().toMutableList()
    val crimeList: List<Crime>
        get() {
            if (_crimeList.isEmpty()) {
                for (i in 0..<100) {
                    val crime = Crime()
                    crime.title = "Crime#$i"
                    crime.isSolved = i % 2 == 0
                    crime.isRequiresPolice = i % 7 == 0 || i % 11 == 0
                    _crimeList += crime
                }
            }
            return _crimeList
        }
}