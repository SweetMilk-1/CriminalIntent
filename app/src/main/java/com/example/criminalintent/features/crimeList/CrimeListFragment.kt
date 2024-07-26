package com.example.criminalintent.features.crimeList

import android.content.Context
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
import com.example.criminalintent.database.entities.Crime
import java.util.UUID

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {
    private val viewModel: CrimeListViewModel by viewModels()

    private lateinit var crimesRecyclerView: RecyclerView
    private var crimeAdapter: CrimeAdapter? = null
    private var callbacks: Callbacks? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        initViews(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.crimeList.observe(viewLifecycleOwner) { crimeList ->
            updateUI(crimeList)
        }
    }

    private fun updateUI(crimeList: List<Crime>) {
        crimeAdapter = CrimeAdapter(crimeList)
        crimesRecyclerView.adapter = crimeAdapter
    }

    private fun initViews(view: View) {
        crimesRecyclerView = view.findViewById(R.id.crimes_recycler_view)
        crimesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        callbacks = null
        super.onDetach()
    }

    companion object {
        fun newInstance() = CrimeListFragment()
    }


    inner class CrimeViewHolder(view: View) : ViewHolder(view), View.OnClickListener {
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
            callbacks?.onClickCrimeListItem(crime.id)
        }
    }

    inner class CrimeAdapter(private val crimeList: List<Crime>) : Adapter<CrimeViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeViewHolder {
            val view = layoutInflater.inflate(R.layout.crime_list_item, parent, false)
            return CrimeViewHolder(view)
        }

        override fun getItemCount(): Int = crimeList.size

        override fun onBindViewHolder(holder: CrimeViewHolder, position: Int) {
            val crime = crimeList[position]
            holder.bind(crime)
        }
    }

    interface Callbacks {
        fun onClickCrimeListItem(id: UUID)
    }
}



