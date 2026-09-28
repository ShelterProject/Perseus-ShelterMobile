package sidev.app.shelter.data.weather

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sidev.app.shelter.data.weather.local.WeatherForecastDao
import sidev.app.shelter.domain.model.WeatherForecast
import sidev.app.shelter.domain.repository.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherForecastDao: WeatherForecastDao,
) : WeatherRepository {

    override fun forecasts(): Flow<List<WeatherForecast>> =
        weatherForecastDao.observeAll().map { list -> list.map { it.toDomain() } }
}
