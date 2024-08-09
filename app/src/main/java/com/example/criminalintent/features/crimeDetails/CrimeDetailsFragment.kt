package com.example.criminalintent.features.crimeDetails

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.Manifest
import androidx.core.content.ContextCompat
import com.example.criminalintent.R
import com.example.criminalintent.common.datepicker.DatePickerFragment
import com.example.criminalintent.common.timepicker.TimePickerFragment
import com.example.criminalintent.database.entities.Crime
import com.google.android.material.snackbar.Snackbar
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
            val queryFields = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone._ID
            )
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
                val contactId = it.getInt(1)

                crime.suspect = suspect
                crime.contactId = contactId

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
    private lateinit var btnCallSuspect: Button

    /*
    * Этот лаунчер для запроса необходимые приложению разрешений.
    * В данном случае это разрешение для просмотра контактов. В случае,
    * если пользователь отклонил запрос доступа, то выведется snackbar
    * с оповещением
    * */
    private var requestMultiplePermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsGranted ->
            val isAllGranted = permissionsGranted.values.all { it }
            if (!isAllGranted) {
                Snackbar.make(
                    requireView(),
                    "Permission denied. Not all permits received",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

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
        btnCallSuspect = view.findViewById(R.id.btn_call_suspect)
    }

    /*
    * Подписываемся на LIveData в ViewModel для того, чтобы UI
    * обновлялся каждый раз, когда в LiveData будет положено
    * новое значение
    * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.crimeLiveData.observe(viewLifecycleOwner) { crime ->
            crime?.let {
                this.crime = crime
                updateUI()
            }
        }
    }

    /*
    * Функция для обновления данных на уровне UI
    * */
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

        if (crime.suspect.isNotBlank()) {
            btnSuspect.text = crime.suspect
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
        setBtnCallSuspectListener()
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

    /*
    * Пример вызова фрагмента диалога для выбора даты и времени.
    * Так же в этом месте добавляются слушатели события для обработки
    * результата работы фрагмента
    * */
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
        ) { _, bundle ->
            val time = bundle.getSerializable(TimePickerFragment.EXTRA_REQUEST_TIME) as Date
            crime.date.hours = time.hours
            crime.date.minutes = time.minutes
            updateUI()
        }
    }

    /*
    * Пример создания слушателя события для CheckBoxView
    * */
    private fun setCbSolvedListener() {
        cbSolved.setOnCheckedChangeListener { _, isChecked ->
            crime.isSolved = isChecked
        }
    }

    /*
    * Пример создания слушателя события для EditTextView
    * */
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

    /*
    * Здесь можно посмотреть пример интента для запуска активити
    * для выбора контакта
    * */
    private fun setBtnSuspectListener() {
        btnSuspect.apply {
            val intent = Intent(
                Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            )
            setOnClickListener {
                suspectActivityLauncher.launch(intent)
            }
            val packageManager = requireActivity().packageManager
            val resolvedActivity = packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            if (resolvedActivity == null)
                isEnabled = false
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

    /*
    * Здесь можно посмотреть пример запроса разрешения. Если доступа нет, то
    * запускается лаунчер.
    * */
    private fun setBtnCallSuspectListener() {
        btnCallSuspect.apply {
            setOnClickListener {
                if (!hasPermissions()) {
                    startPermissionRequest()
                }
                if (!hasPermissions())
                    return@setOnClickListener
                callSuspect()
            }
        }
    }

    /*
    * В этой функции можно посмотреть пример неявного интента для
    * запуска активити телефонного вызова
    * */
    private fun callSuspect() {
        val contactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val fields = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val selection = "${ContactsContract.CommonDataKinds.Phone._ID} = ?"
        val selectionArgs = arrayOf(crime.contactId.toString())
        requireActivity().contentResolver.query(
            contactsUri,
            fields,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.count == 0) {
                Toast.makeText(context, "Contact not found", Toast.LENGTH_SHORT).show()
                return
            }
            cursor.moveToFirst()
            val number = cursor.getString(0)

            //Интент
            val numberUri = Uri.parse("tel:$number")
            val callIntent = Intent(
                Intent.ACTION_DIAL
            ).apply {
                data = numberUri
            }
            startActivity(callIntent)
        }
    }

    /*
    * Эта функция проверяет, что пользователь дал все необходимые
    * разрешения
    * */
    private fun hasPermissions(): Boolean {
        return PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /*
    * Эта функция запускает лаунчер, в котором пользователю будет
    * предложено выбрать: разрешает ли он доступ или нет
    * */
    private fun startPermissionRequest() {
        requestMultiplePermissionsLauncher.launch(PERMISSIONS)
    }

    companion object {
        private val PERMISSIONS = arrayOf(
            Manifest.permission.READ_CONTACTS
        )

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