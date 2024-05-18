package com.example.environmental_analyzer

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.environmental_analyzer.Entity.AirPollution
import com.example.environmental_analyzer.Entity.UVRadiation
import com.example.environmental_analyzer.Entity.Weather

@Database(entities = [Weather :: class, AirPollution :: class, UVRadiation :: class], version = 1)
abstract class MainDb : RoomDatabase() {
    abstract fun getDao(): Dao
    companion object{

        fun getDb(context: Context): MainDb{
            return Room.databaseBuilder(
                context.applicationContext,
                MainDb :: class.java,
                "Environmental_Analyzer_Db"
            ).build()
        }

    }
}