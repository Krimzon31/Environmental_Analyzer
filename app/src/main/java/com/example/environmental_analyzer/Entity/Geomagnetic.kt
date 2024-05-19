package com.example.environmental_analyzer.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Geomagnetic")
data class Geomagnetic(

    @PrimaryKey(autoGenerate = true)
    val idGeomagnetic: Int? = null,

    @ColumnInfo(name = "city")
    val city: String,

    @ColumnInfo(name = "date")
    val date: Int,

    @ColumnInfo(name = "meteoIndex")
    val meteoIndex: String,

    @ColumnInfo(name = "geomagnetic")
    val geomagnetic: String,

    @ColumnInfo(name = "sunGeomagnetic")
    val sunGeomagnetic: String,

    @ColumnInfo(name = "davl")
    val davl: String,

    @ColumnInfo(name = "vlag")
    val vlag: String,

    @ColumnInfo(name = "veter")
    val veter: String,

    @ColumnInfo(name = "porVeter")
    val porVeter: String,

    @ColumnInfo(name = "obl")
    val obl: String,

    @ColumnInfo(name = "imageGeo")
    val imageGeo: String
)
