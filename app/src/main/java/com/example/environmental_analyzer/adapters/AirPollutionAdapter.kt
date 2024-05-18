package com.example.environmental_analyzer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.environmental_analyzer.MAIN
import com.example.environmental_analyzer.R

class AirPollutionAdapter(private val AirList: List<AirPollutionModel>) : RecyclerView.Adapter<AirPollutionAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.air_pollution_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = AirList[position]
        holder.tvDateItemAir.text = currentItem.date
        holder.tvAQIItem.text = currentItem.aqi
        holder.tvPollutionLevelItem.text = currentItem.pollutionLevel
        when(currentItem.pollutionLevel){
            "Хорошо" -> holder.imgAirItem.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVGreenIndicate))
            "Средне" -> holder.imgAirItem.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVYellowIndicate))
            "Вредно для уязвимых групп"-> holder.imgAirItem.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVOrangeIndicate))
            "Вредно"-> holder.imgAirItem.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVRedIndicate))
            "Очень вредно" -> holder.imgAirItem.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVPerpleIndicate))
            "Опасно" -> holder.imgAirItem.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.danger))
        }

    }

    override fun getItemCount(): Int {
        return AirList.size
    }


    class MyViewHolder(item: View) : RecyclerView.ViewHolder(item){

        val tvDateItemAir : TextView = item.findViewById(R.id.tvDateItemAir)
        val tvAQIItem : TextView = item.findViewById(R.id.tvAQIItem)
        val tvPollutionLevelItem : TextView = item.findViewById(R.id.tvPollutionLevelItem)
        val imgAirItem : ImageView = item.findViewById(R.id.imgAirItem)
    }

}