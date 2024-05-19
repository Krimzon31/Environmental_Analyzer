package com.example.environmental_analyzer.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "City")
data class City(
    @PrimaryKey(autoGenerate = true)
    val idAirPollution: Int? = null,

    @ColumnInfo(name = "cityRus")
    val cityRus: String,

    @ColumnInfo(name = "cityEng")
    val cityEng: String,
)
