package id.my.shelter.app.data.weather

import id.my.shelter.app.data.weather.local.WeatherForecastEntity
import id.my.shelter.app.data.weather.remote.WeatherForecastDto
import id.my.shelter.app.domain.model.WeatherForecast
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
