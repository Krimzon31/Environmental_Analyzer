package com.example.environmental_analyzer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.environmental_analyzer.Models.WeatherModel
import com.example.environmental_analyzer.R
import com.example.environmental_analyzer.databinding.ListItemBinding
import com.squareup.picasso.Picasso

class WeatherAdapter : ListAdapter<WeatherModel, WeatherAdapter.Holder>(Comparator()) {

    class Holder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ListItemBinding.bind(view)

        fun bind(item: WeatherModel) = with(binding){
            tvDateTime.text = item.date
            tvTemp.text = item.currentTemp.ifEmpty { "${item.currentMaxTemp}°C/ ${item.currentMinTemp}°C" }
            conditionView.text = item.condition
            Picasso.get().load("https:"+item.imageUrl).into(image)
        }
    }

    class Comparator :DiffUtil.ItemCallback<WeatherModel>(){
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}