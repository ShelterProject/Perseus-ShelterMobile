package sidev.app.shelter.data.weather

import sidev.app.shelter.data.weather.local.WeatherForecastEntity
import sidev.app.shelter.data.weather.remote.WeatherForecastDto
import sidev.app.shelter.domain.model.WeatherForecast
import java.time.LocalDate

fun WeatherForecastDto.toEntity() = WeatherForecastEntity(
    date = date,
    temperature = temperature,
    humidity = humidity,
    rainfall = rainfall,
    windSpeed = windSpeed,
    uvIndex = uvIndex,
    condition = condition,
)

fun WeatherForecastEntity.toDomain() = WeatherForecast(
    date = LocalDate.parse(date),
    temperature = temperature,
    humidity = humidity,
    rainfall = rainfall,
    windSpeed = windSpeed,
    uvIndex = uvIndex,
    condition = condition,
)
