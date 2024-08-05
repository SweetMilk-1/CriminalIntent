package com.example.criminalintent.common.datepicker

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import java.util.Date

private const val ARG_INITIAL_DATE = "ARG_INITIAL_DATE"

class DatePickerFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->

            targetFragment?.let {
                (targetFragment as Callbacks).onDateSelected(
                    Date(year, month, day)
                )
            }
        }

        val date = arguments?.getSerializable(ARG_INITIAL_DATE) as Date

        val calendar = Calendar.getInstance()
        calendar.time = date

        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(
            requireContext(),
            dateListener,
            initialYear,
            initialMonth,
            initialDay
        )
    }

    companion object {
        fun newInstance(date: Date): DatePickerFragment {
            val arguments = Bundle().apply {
                putSerializable(ARG_INITIAL_DATE, date)
            }
            return DatePickerFragment().apply {
                this.arguments = arguments
            }
        }
    }

    interface Callbacks {
        fun onDateSelected(date: Date)
    }
}