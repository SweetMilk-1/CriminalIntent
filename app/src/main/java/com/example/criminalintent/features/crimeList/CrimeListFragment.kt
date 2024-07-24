package com.example.criminalintent.features.crimeList

import android.os.Bundle
import android.icu.text.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.criminalintent.R
import com.example.criminalintent.model.Crime
import java.util.Locale

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {
    private val viewModel: CrimeListViewModel by viewModels()

    private lateinit var crimesRecyclerView: RecyclerView
    private var crimeAdapter: CrimeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Crimes count: ${viewModel.crimeList.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        initViews(view)

        updateUI()
        return view
    }

    private fun updateUI() {
        crimeAdapter = CrimeAdapter(viewModel.crimeList)
        crimesRecyclerView.adapter = crimeAdapter
    }

    private fun initViews(view: View) {
        crimesRecyclerView = view.findViewById(R.id.crimes_recycler_view)
        crimesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    companion object {
        fun newInstance() = CrimeListFragment()
    }


    open inner class CrimeViewHolder(view: View) : ViewHolder(view), View.OnClickListener {
        private val tvCrimeTitle: TextView = view.findViewById(R.id.tv_crime_title)
        private val tvCrimeDate: TextView = view.findViewById(R.id.tv_crime_date)
        private val ivCrimeSolved: ImageView = view.findViewById(R.id.iv_crime_solved)

        protected var crime: Crime? = null
            private set

        init {
            itemView.setOnClickListener(this)
        }

        open fun bind(crime: Crime) {
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
            Toast.makeText(
                itemView.context,
                "${crime?.title} was pressed",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    inner class RequiresPoliceCrimeViewHolder(view: View) : CrimeViewHolder(view) {

        private val btnPolice : Button = itemView.findViewById(R.id.btn_police)

        override fun bind(crime: Crime) {
            super.bind(crime)
            btnPolice.visibility = if (crime.isRequiresPolice && !crime.isSolved) View.VISIBLE else View.GONE
        }

        override fun onClick(v: View?) {
            Toast.makeText(
                itemView.context,
                "${crime?.title} was pressed (require police, be carefully)",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    inner class CrimeAdapter(private val crimeList: List<Crime>) : Adapter<CrimeViewHolder>() {
        private val REQUIRE_POLICE_CRIME_TYPE = 1
        private val SIMPLE_CRIME_TYPE = 0

        override fun getItemViewType(position: Int): Int {
            val crime = crimeList[position]
            return when {
                crime.isRequiresPolice -> REQUIRE_POLICE_CRIME_TYPE
                else -> SIMPLE_CRIME_TYPE
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeViewHolder {

            return when (viewType) {
                SIMPLE_CRIME_TYPE -> {
                    val view = layoutInflater.inflate(R.layout.crime_list_item, parent, false)
                    CrimeViewHolder(view)
                }

                REQUIRE_POLICE_CRIME_TYPE -> {
                    val view = layoutInflater.inflate(
                        R.layout.requires_police_crime_list_item,
                        parent,
                        false
                    )
                    RequiresPoliceCrimeViewHolder(view)
                }

                else -> throw IllegalAccessException()
            }
        }

        override fun getItemCount(): Int = crimeList.size

        override fun onBindViewHolder(holder: CrimeViewHolder, position: Int) {
            val crime = crimeList[position]
            holder.bind(crime)
        }
    }
}

