package com.example.environmental_analyzer

import android.R
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Spinner

object DialogManager {
    fun recomendationDialog(context: Context, massage: String){
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle("Рекомендация")
        dialog.setMessage(massage)
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Закрыть"){ _, _->
            dialog.dismiss()
        }
        dialog.show()
    }

    fun locationSettingsDialog(context: Context, listener: Listener, items: List<String>) {
        val layoutInflater = LayoutInflater.from(context)
        val builder = AlertDialog.Builder(context)
        val dialogView = layoutInflater.inflate(android.R.layout.simple_spinner_dropdown_item, null)
        val dialog = builder.create()
        val spCity = Spinner(context)

        // Создаем адаптер и устанавливаем его для Spinner
        val adapter = ArrayAdapter(context, R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCity.adapter = adapter

        dialog.setTitle("Выберите город")

        dialog.setView(spCity)

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "ОК") { _, _ ->
            listener.onClick(spCity.selectedItem.toString())
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена") { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    interface Listener{
        fun onClick(city: String)
    }
}