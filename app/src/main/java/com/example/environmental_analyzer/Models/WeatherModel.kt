package com.example.environmental_analyzer.Models

data class WeatherModel (
    val city: String,
    val date: String,
    val condition: String,
    val imageUrl: String,
    val currentTemp: String,
    val currentMaxTemp: String,
    val currentMinTemp: String,
    val hours: String
)