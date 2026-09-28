package id.my.shelter.app.data.weather.remote

import kotlinx.serialization.Serializable

@Serializable
data class WeatherForecastDto(
    val date: String,
    val temperature: Double,
    val humidity: Double,
    val rainfall: Double,
    val windSpeed: Double,
    val uvIndex: Double,
    val condition: String,
)
