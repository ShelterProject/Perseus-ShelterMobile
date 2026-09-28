package id.my.shelter.app.data.weather

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import id.my.shelter.app.data.weather.local.WeatherForecastDao
import id.my.shelter.app.domain.model.WeatherForecast
import id.my.shelter.app.domain.repository.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherForecastDao: WeatherForecastDao,
) : WeatherRepository {

    override fun forecasts(): Flow<List<WeatherForecast>> =
        weatherForecastDao.observeAll().map { list -> list.map { it.toDomain() } }
}
