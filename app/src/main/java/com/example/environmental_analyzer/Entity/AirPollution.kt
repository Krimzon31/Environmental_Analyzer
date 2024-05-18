package com.example.environmental_analyzer.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AirPollution")
data class AirPollution(

    @PrimaryKey(autoGenerate = true)
    val idAirPollution: Int? = null,

    @ColumnInfo(name = "city")
    val city: String,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "aqi")
    val aqi: String,

    @ColumnInfo(name = "pollutionLevel")
    val pollutionLevel: String,

    @ColumnInfo(name = "mainPolluter")
    val mainPolluter: String,

    @ColumnInfo(name = "imagePollution")
    val imagePollution: String
)
