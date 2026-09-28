package id.my.shelter.app.core.di

import androidx.room.Database
import androidx.room.RoomDatabase
import id.my.shelter.app.data.disaster.local.EarthquakeDao
import id.my.shelter.app.data.disaster.local.EarthquakeEntity
import id.my.shelter.app.data.disaster.local.FloodDao
import id.my.shelter.app.data.disaster.local.FloodEntity
import id.my.shelter.app.data.disaster.local.ForestFireDao
import id.my.shelter.app.data.disaster.local.ForestFireEntity
import id.my.shelter.app.data.disaster.local.LandslideDao
import id.my.shelter.app.data.disaster.local.LandslideEntity
import id.my.shelter.app.data.sync.SyncMetaDao
import id.my.shelter.app.data.sync.SyncMetaEntity
import id.my.shelter.app.data.weather.local.WeatherForecastDao
import id.my.shelter.app.data.weather.local.WeatherForecastEntity

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
