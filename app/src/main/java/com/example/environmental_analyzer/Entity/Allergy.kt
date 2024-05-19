package com.example.environmental_analyzer.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Allergy")
data class Allergy(

    @PrimaryKey(autoGenerate = true)
    val idGeomagnetic: Int? = null,

    @ColumnInfo(name = "city")
    val city: String,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "seson")
    val seson: String,

    @ColumnInfo(name = "mainAllerg")
    val mainAllerg: String,

    @ColumnInfo(name = "allergyImg")
    val allergyImg: String
)
