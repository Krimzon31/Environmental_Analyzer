package com.example.environmental_analyzer

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.environmental_analyzer.Entity.AirPollution
import com.example.environmental_analyzer.Entity.Weather
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Insert
    fun insertWeather(weather: Weather)

    @Query("SELECT * FROM weather")
    fun getWeather(): Flow<List<Weather>>

    @Query("SELECT COUNT(*) FROM weather")
    fun countTableRowsWeather(): LiveData<Int>

    @Query("DELETE FROM weather")
    fun deleteAllWeather()


    @Insert
    fun insertAirPollution(airPollution: AirPollution)

    @Query("SELECT * FROM airpollution")
    fun getAirPollution(): Flow<List<AirPollution>>

    @Query("SELECT COUNT(*) FROM airpollution")
    fun countTableRowsAirPollution(): LiveData<Int>

    @Query("DELETE FROM airpollution")
    fun deleteAirPollution()
}