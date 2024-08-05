package com.example.criminalintent.features.crimeList

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.R
import com.example.criminalintent.database.entities.Crime

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {
    private val viewModel: CrimeListViewModel by viewModels()

    private lateinit var crimeAdapter : CrimeAdapter
    private lateinit var crimesRecyclerView: RecyclerView
    private var callbacks: Callbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        initViews(view)

        customizeCrimesRecyclerView()

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.new_crime -> {
                createNewCrime()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createNewCrime() {
        val crime = Crime()
        viewModel.addCrime(crime)
        callbacks?.onCrimeSelected(crime.id)
    }

    private fun customizeCrimesRecyclerView() {
        crimeAdapter = CrimeAdapter(requireContext(), callbacks)
        crimesRecyclerView.adapter = crimeAdapter
        viewModel.crimeList.observe(viewLifecycleOwner) { crimeList ->
            crimeAdapter.submitList(crimeList)
        }
    }

    private fun initViews(view: View) {
        crimesRecyclerView = view.findViewById(R.id.crimes_recycler_view)
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

    interface Callbacks :
        CrimeViewHolder.Callbacks
}



