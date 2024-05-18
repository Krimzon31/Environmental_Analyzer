package com.example.environmental_analyzer.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Weather")
data class Weather(
    @PrimaryKey(autoGenerate = true)
    val idWeather: Int? = null,

    @ColumnInfo(name = "city")
    val city: String,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "condition")
    val condition: String,

    @ColumnInfo(name = "imageUrl")
    val imageUrl: String,

    @ColumnInfo(name = "currentTemp")
    val currentTemp: String,

    @ColumnInfo(name = "currentMaxTemp")
    val currentMaxTemp: String,

    @ColumnInfo(name = "currentMinTemp")
    val currentMinTemp: String

)
