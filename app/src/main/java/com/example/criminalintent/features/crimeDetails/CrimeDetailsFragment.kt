package com.example.criminalintent.features.crimeDetails

import android.icu.text.DateFormat
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
import androidx.fragment.app.viewModels
import com.example.criminalintent.R
import com.example.criminalintent.common.datepicker.DatePickerFragment
import com.example.criminalintent.common.timepicker.TimePickerFragment
import com.example.criminalintent.database.entities.Crime
import java.util.Date
import java.util.UUID

private const val ARG_CRIME_ID = "ARG_CRIME_ID"

private const val DIALOG_DATE_PICKER = "DIALOG_DATE_PICKER"
private const val DIALOG_TIME_PICKER = "DIALOG_TIME_PICKER"
private const val REQUEST_DATE = 0
private const val REQUEST_TIME = 1

class CrimeDetailsFragment : Fragment(),
    DatePickerFragment.Callbacks, TimePickerFragment.Callbacks {
    private val viewModel: CrimeDetailsViewModel by viewModels()

    private lateinit var crime: Crime

    private lateinit var etCrimeTitle: EditText
    private lateinit var btnCrimeDate: Button
    private lateinit var btnCrimeTime: Button
    private lateinit var cbSolved: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crimeId = arguments?.getSerializable(ARG_CRIME_ID) as UUID

        viewModel.loadCrime(crimeId)
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
        btnCrimeTime = view.findViewById(R.id.btn_crime_time)
        cbSolved = view.findViewById(R.id.cb_solved)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.crimeLiveData.observe(viewLifecycleOwner) { crime ->
            crime?.let {
                this.crime = crime
                updateUI()
            }
        }
    }

    private fun updateUI() {
        etCrimeTitle.setText(crime.title)

        val datePattern = "${DateFormat.WEEKDAY}, ${DateFormat.YEAR_ABBR_MONTH_DAY}"
        val timePattern = DateFormat.HOUR24_MINUTE

        val dateString = DateFormat
            .getPatternInstance(datePattern)
            .format(crime.date)
        val timeString = DateFormat
            .getPatternInstance(timePattern)
            .format(crime.date)

        btnCrimeDate.text = dateString
        btnCrimeTime.text = timeString

        cbSolved.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
    }

    override fun onStart() {
        super.onStart()
        setListenersOnViews()
    }

    override fun onStop() {
        super.onStop()
        viewModel.updateCrime(crime)
    }

    private fun setListenersOnViews() {
        setEtCrimeTitleListener()
        setCbSolvedListener()
        setBtnCrimeDateListener()
        setBtnCrimeTimeListener()
    }

    private fun setBtnCrimeDateListener() {
        btnCrimeDate.apply {
            setOnClickListener {
                DatePickerFragment.newInstance(crime.date).apply {
                    setTargetFragment(this@CrimeDetailsFragment, REQUEST_DATE)
                    show(
                        this@CrimeDetailsFragment.getParentFragmentManager(), DIALOG_DATE_PICKER
                    )
                }
            }
        }
    }

    private fun setBtnCrimeTimeListener() {
        btnCrimeTime.apply {
            setOnClickListener {
                TimePickerFragment.newInstance(crime.date).apply {
                    setTargetFragment(this@CrimeDetailsFragment, REQUEST_TIME)
                    show(
                        this@CrimeDetailsFragment.getParentFragmentManager(), DIALOG_TIME_PICKER
                    )
                }
            }
        }
    }

    private fun setCbSolvedListener() {
        cbSolved.setOnCheckedChangeListener { _, isChecked ->
            crime.isSolved = isChecked
        }
    }

    private fun setEtCrimeTitleListener() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        etCrimeTitle.addTextChangedListener(textWatcher)
    }

    companion object {
        fun newInstance(id: UUID): CrimeDetailsFragment {
            val fragment = CrimeDetailsFragment()

            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, id)

            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDateSelected(date: Date) {
        crime.date.date = date.date
        updateUI()
    }

    override fun onTimeSelected(time: Date) {
        crime.date.hours = time.hours
        crime.date.minutes = time.minutes
        updateUI()
    }
}