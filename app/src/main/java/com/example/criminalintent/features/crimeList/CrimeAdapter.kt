package com.example.criminalintent.features.crimeList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.criminalintent.R
import com.example.criminalintent.database.entities.Crime

/*
* Адаптер для листа преступлений. Отвечает за создание ViewHolder
* и привязки к нему данных. В себя он принимает масив элементов и оптимальным
* образом создает или переиспользует ViewHoleder
* */
class CrimeAdapter(
    context: Context,
    private val callbacks: CrimeViewHolder.Callbacks?
) : ListAdapter<Crime, CrimeViewHolder>(CrimeDiffUtilItemCallbacks()) {
    private val layoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeViewHolder {
        val view = layoutInflater.inflate(R.layout.crime_list_item, parent, false)
        return CrimeViewHolder(view, callbacks)
    }

    override fun onBindViewHolder(holder: CrimeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

/*
* Это коллбэк для определения разницы между исходным и новыми элементами массива
* Благодаря ему нет необходимости пересоздавать на каждое изменение массива новый
* адаптер, а также благодаря нему можно получить бесплатную анимацию)
* */
class CrimeDiffUtilItemCallbacks : DiffUtil.ItemCallback<Crime>() {
    override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
        return oldItem == newItem
    }
}