package com.example.criminalintent.common.timepicker

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

import java.util.Date
private const val ARG_INITIAL_TIME = "ARG_INITIAL_TIME"
class TimePickerFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val time = arguments?.getSerializable(ARG_INITIAL_TIME) as Date

        val timeListener = TimePickerDialog.OnTimeSetListener {_, hours, minute ->
            val resultTime = Date().apply {
                this.hours = hours
                this.minutes = minute
            }
            (targetFragment as Callbacks).onTimeSelected(resultTime)
        }

        return TimePickerDialog(
            context,
            timeListener,
            time.hours,
            time.minutes,
            true
        )
    }

    companion object {
        fun newInstance(time: Date): TimePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_INITIAL_TIME, time)
            }
            val fragment = TimePickerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    interface Callbacks{
        fun onTimeSelected(time: Date)
    }
}