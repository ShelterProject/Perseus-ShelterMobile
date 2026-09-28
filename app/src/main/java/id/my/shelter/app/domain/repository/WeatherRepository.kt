package id.my.shelter.app.domain.repository

import kotlinx.coroutines.flow.Flow
import id.my.shelter.app.domain.model.WeatherForecast

interface WeatherRepository {
    fun forecasts(): Flow<List<WeatherForecast>>
}
