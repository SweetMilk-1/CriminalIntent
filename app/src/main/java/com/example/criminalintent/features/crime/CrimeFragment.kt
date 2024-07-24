package com.example.criminalintent.features.crime

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.criminalintent.R
import com.example.criminalintent.model.Crime

class CrimeFragment: Fragment() {
    private lateinit var crime: Crime

    private lateinit var etCrimeTitle: EditText
    private lateinit var btnCrimeDate: Button
    private lateinit var cbSolved: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        initViews(view)
        return view
    }

    private fun initViews(view: View) {
        etCrimeTitle = view.findViewById(R.id.et_crime_title)
        btnCrimeDate = view.findViewById(R.id.btn_crime_date)
        cbSolved = view.findViewById(R.id.cb_solved)

        setCrimeDateOnButton()
    }

    private fun setCrimeDateOnButton() {
        btnCrimeDate.apply {
            text = crime.date.toString()
            isEnabled = false
        }
    }

    override fun onStart() {
        super.onStart()
        setListenersOnViews()
    }

    private fun setListenersOnViews() {
        setEtCrimeTitleListener()
        setCbSolvedListener()
    }

    private fun setCbSolvedListener() {
        cbSolved.setOnCheckedChangeListener{ _, isChecked ->
            crime.isSolved = isChecked
        }
    }

    private fun setEtCrimeTitleListener() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        etCrimeTitle.addTextChangedListener(textWatcher)
    }
}