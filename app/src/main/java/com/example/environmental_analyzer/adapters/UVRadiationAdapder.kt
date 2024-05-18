package com.example.environmental_analyzer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.environmental_analyzer.MAIN
import com.example.environmental_analyzer.Models.UVRadiationModel
import com.example.environmental_analyzer.R

class UVRadiationAdapder (private val UVList: List<UVRadiationModel>) : RecyclerView.Adapter<UVRadiationAdapder.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.uv_radiation_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = UVList[position]
        holder.tvDateUVItem.text = currentItem.date
        holder.tvUVlevelItem.text = currentItem.UVLevel
        holder.tvUVIndexItem.text = currentItem.UVIndex
        when(currentItem.UVLevel){
            "низкий" -> holder.imgUVRadItem.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVGreenIndicate))
            "умеренный" -> holder.imgUVRadItem.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVYellowIndicate))
            "высокий"-> holder.imgUVRadItem.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVOrangeIndicate))
            "очень высокий"-> holder.imgUVRadItem.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVRedIndicate))
            "экстремальный" -> holder.imgUVRadItem.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVPerpleIndicate))
        }

    }

    override fun getItemCount(): Int {
        return UVList.size
    }


    class MyViewHolder(item: View) : RecyclerView.ViewHolder(item){

        val tvDateUVItem : TextView = item.findViewById(R.id.tvDateUVItem)
        val tvUVlevelItem : TextView = item.findViewById(R.id.tvUVlevelItem)
        val tvUVIndexItem : TextView = item.findViewById(R.id.tvUVIndexItem)
        val imgUVRadItem : ImageView = item.findViewById(R.id.imgUVRadItem)
    }

}