package com.example.criminalintent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.criminalintent.features.crimeDetails.CrimeDetailsFragment
import com.example.criminalintent.features.crimeList.CrimeListFragment
import com.example.criminalintent.features.crimeList.CrimeListViewModel
import java.util.UUID

class MainActivity : AppCompatActivity(), CrimeListFragment.Callbacks{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = CrimeListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onClickCrimeListItem(id: UUID) {
        val fragment = CrimeDetailsFragment.newInstance(id)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}