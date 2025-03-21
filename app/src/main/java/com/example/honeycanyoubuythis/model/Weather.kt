package com.example.honeycanyoubuythis.model
import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val current: CurrentWeather
)

data class CurrentWeather(
    @SerializedName("temp_c")
    val tempC: Double,
    val condition: Condition
)

data class Condition(
    val text: String,
)