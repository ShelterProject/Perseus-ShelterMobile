package sidev.app.shelter.domain.repository

import kotlinx.coroutines.flow.Flow
import sidev.app.shelter.domain.model.WeatherForecast

interface WeatherRepository {
    fun forecasts(): Flow<List<WeatherForecast>>
}
