package com.example.criminalintent.features.crimeDetails

import android.app.Activity
import android.content.Intent
import android.icu.text.DateFormat
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.criminalintent.R
import com.example.criminalintent.common.datepicker.DatePickerFragment
import com.example.criminalintent.common.timepicker.TimePickerFragment
import com.example.criminalintent.database.entities.Crime
import java.security.InvalidParameterException
import java.util.Date
import java.util.UUID

private const val ARG_CRIME_ID = "ARG_CRIME_ID"

private const val DIALOG_DATE_PICKER = "DIALOG_DATE_PICKER"
private const val DIALOG_TIME_PICKER = "DIALOG_TIME_PICKER"

class CrimeDetailsFragment : Fragment() {
    private val viewModel: CrimeDetailsViewModel by viewModels()
    private val suspectActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                return@registerForActivityResult
            }
            val contactUri: Uri =
                result.data?.data ?: throw InvalidParameterException("Uri can not be null")
            val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
            val cursor = requireActivity().contentResolver
                .query(
                    contactUri,
                    queryFields,
                    null,
                    null,
                    null
                )
            cursor?.use {
                if (it.count == 0)
                    return@registerForActivityResult
                it.moveToFirst()
                val suspect = it.getString(0)
                crime.suspect = suspect
                viewModel.updateCrime(crime)
                btnSuspect.text = suspect
            }
        }

    private lateinit var crime: Crime

    private lateinit var etCrimeTitle: EditText
    private lateinit var btnCrimeDate: Button
    private lateinit var btnCrimeTime: Button
    private lateinit var cbSolved: CheckBox
    private lateinit var btnSendReport: Button
    private lateinit var btnSuspect: Button

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
        btnSendReport = view.findViewById(R.id.btn_send_report)
        btnSuspect = view.findViewById(R.id.btn_suspect)
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
        setBtnSendReportListener()
        setBtnSuspectListener()
    }

    private fun setBtnCrimeDateListener() {
        btnCrimeDate.apply {
            setOnClickListener {
                DatePickerFragment.newInstance(crime.date).apply {
                    show(
                        this@CrimeDetailsFragment.getParentFragmentManager(), DIALOG_DATE_PICKER
                    )
                }
            }
        }
        parentFragmentManager.setFragmentResultListener(
            DatePickerFragment.REQUEST_DATE,
            viewLifecycleOwner
        ) { key, bundle ->
            val date = bundle.getSerializable(DatePickerFragment.EXTRA_REQUEST_DATE) as Date
            crime.date.date = date.date
            updateUI()
        }
    }

    private fun setBtnCrimeTimeListener() {
        btnCrimeTime.apply {
            setOnClickListener {
                TimePickerFragment.newInstance(crime.date).apply {
                    show(
                        this@CrimeDetailsFragment.getParentFragmentManager(), DIALOG_TIME_PICKER
                    )
                }
            }
        }
        parentFragmentManager.setFragmentResultListener(
            TimePickerFragment.REQUEST_TIME,
            viewLifecycleOwner
        ) { key, bundle ->
            val time = bundle.getSerializable(TimePickerFragment.EXTRA_REQUEST_TIME) as Date
            crime.date.hours = time.hours
            crime.date.minutes = time.minutes
            updateUI()
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

    private fun setBtnSendReportListener() {
        btnSendReport.setOnClickListener {
            val report = getCrimeReport()
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, report)
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }
            val chooserIntent = Intent.createChooser(
                intent,
                getString(R.string.crime_report)
            )
            startActivity(chooserIntent)
        }
    }

    private fun setBtnSuspectListener() {
        btnSuspect.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            suspectActivityLauncher.launch(intent)
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved)
            getString(R.string.crime_report_solved)
        else
            getString(R.string.crime_report_not_solved)

        val dateString = DateFormat
            .getPatternInstance(DateFormat.YEAR_MONTH_DAY)
            .format(crime.date)

        val suspectString = if (crime.suspect.isBlank())
            getString(R.string.crime_report_no_suspect)
        else
            getString(R.string.crime_report_suspect, crime.suspect)

        return getString(
            R.string.crime_report,
            crime.title, dateString, solvedString, suspectString
        )
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
}