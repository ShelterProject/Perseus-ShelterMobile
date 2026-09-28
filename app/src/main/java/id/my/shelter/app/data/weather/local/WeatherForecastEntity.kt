package id.my.shelter.app.data.weather.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "weather_forecasts")
data class WeatherForecastEntity(
    @PrimaryKey val date: String, // ISO-8601, one forecast row per date
    val temperature: Double,
    val humidity: Double,
    val rainfall: Double,
    val windSpeed: Double,
    val uvIndex: Double,
    val condition: String,
)

@Dao
interface WeatherForecastDao {
    @Query("SELECT * FROM weather_forecasts ORDER BY date DESC")
    fun observeAll(): Flow<List<WeatherForecastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WeatherForecastEntity>)

    @Query("DELETE FROM weather_forecasts")
    suspend fun clear()
}
