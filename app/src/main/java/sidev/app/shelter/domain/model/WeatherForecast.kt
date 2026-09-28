package sidev.app.shelter.domain.model

import java.time.LocalDate

data class WeatherForecast(
    val date: LocalDate,
    val temperature: Double,
    val humidity: Double,
    val rainfall: Double,
    val windSpeed: Double,
    val uvIndex: Double,
    val condition: String,
)
