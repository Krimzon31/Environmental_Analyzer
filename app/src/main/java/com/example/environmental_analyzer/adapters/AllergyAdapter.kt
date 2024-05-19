package com.example.environmental_analyzer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.environmental_analyzer.Models.AllergyModel
import com.example.environmental_analyzer.R

class AllergyAdapter (private val AllList: List<AllergyModel>) : RecyclerView.Adapter<AllergyAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.allergy_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = AllList[position]
        holder.tvAllergyPlant.text = currentItem.plant_name

    }

    override fun getItemCount(): Int {
        return AllList.size
    }


    class MyViewHolder(item: View) : RecyclerView.ViewHolder(item){

        val tvAllergyPlant : TextView = item.findViewById(R.id.tvAllergyPlant)
    }

}