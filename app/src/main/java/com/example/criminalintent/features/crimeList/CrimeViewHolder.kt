package com.example.criminalintent.features.crimeList

import android.icu.text.DateFormat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.R
import com.example.criminalintent.database.entities.Crime
import java.util.UUID

class CrimeViewHolder(
    view: View,
    private val crimeListFragmentCallbacks: Callbacks?
) : RecyclerView.ViewHolder(view),
    View.OnClickListener {
    private val tvCrimeTitle: TextView = view.findViewById(R.id.tv_crime_title)
    private val tvCrimeDate: TextView = view.findViewById(R.id.tv_crime_date)
    private val ivCrimeSolved: ImageView = view.findViewById(R.id.iv_crime_solved)

    private lateinit var crime: Crime

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(crime: Crime) {
        this.crime = crime

        val datePattern = "${DateFormat.WEEKDAY}, ${DateFormat.YEAR_ABBR_MONTH_DAY}"
        val dateString = DateFormat
            .getPatternInstance(datePattern)
            .format(crime.date)

        tvCrimeTitle.text = crime.title
        tvCrimeDate.text = dateString
        ivCrimeSolved.visibility = if (crime.isSolved) View.VISIBLE else View.GONE
    }

    override fun onClick(v: View?) {
        crimeListFragmentCallbacks?.onCrimeSelected(crime.id)
    }

    interface Callbacks {
        fun onCrimeSelected(id: UUID)
    }
}