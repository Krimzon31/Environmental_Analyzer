package com.example.environmental_analyzer.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UVRadiation")
data class UVRadiation(
    @PrimaryKey(autoGenerate = true)
    val idUVRadiation: Int? = null,

    @ColumnInfo(name = "city")
    val city: String,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "UVIndex")
    val UVIndex: String,

    @ColumnInfo(name = "UVLevel")
    val UVLevel: String,

    @ColumnInfo(name = "imageUV")
    val imageUV: String
)
