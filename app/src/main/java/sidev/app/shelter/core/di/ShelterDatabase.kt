package sidev.app.shelter.core.di

import androidx.room.Database
import androidx.room.RoomDatabase
import sidev.app.shelter.data.disaster.local.EarthquakeDao
import sidev.app.shelter.data.disaster.local.EarthquakeEntity
import sidev.app.shelter.data.disaster.local.FloodDao
import sidev.app.shelter.data.disaster.local.FloodEntity
import sidev.app.shelter.data.disaster.local.ForestFireDao
import sidev.app.shelter.data.disaster.local.ForestFireEntity
import sidev.app.shelter.data.disaster.local.LandslideDao
import sidev.app.shelter.data.disaster.local.LandslideEntity
import sidev.app.shelter.data.sync.SyncMetaDao
import sidev.app.shelter.data.sync.SyncMetaEntity
import sidev.app.shelter.data.weather.local.WeatherForecastDao
import sidev.app.shelter.data.weather.local.WeatherForecastEntity

@Database(
    entities = [
        EarthquakeEntity::class,
        LandslideEntity::class,
        FloodEntity::class,
        ForestFireEntity::class,
        WeatherForecastEntity::class,
        SyncMetaEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class ShelterDatabase : RoomDatabase() {
    abstract fun earthquakeDao(): EarthquakeDao
    abstract fun landslideDao(): LandslideDao
    abstract fun floodDao(): FloodDao
    abstract fun forestFireDao(): ForestFireDao
    abstract fun weatherForecastDao(): WeatherForecastDao
    abstract fun syncMetaDao(): SyncMetaDao
}
