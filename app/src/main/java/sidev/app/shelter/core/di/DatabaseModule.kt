package sidev.app.shelter.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import sidev.app.shelter.data.disaster.local.EarthquakeDao
import sidev.app.shelter.data.disaster.local.FloodDao
import sidev.app.shelter.data.disaster.local.ForestFireDao
import sidev.app.shelter.data.disaster.local.LandslideDao
import sidev.app.shelter.data.sync.SyncMetaDao
import sidev.app.shelter.data.weather.local.WeatherForecastDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ShelterDatabase =
        Room.databaseBuilder(context, ShelterDatabase::class.java, "shelter.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideEarthquakeDao(db: ShelterDatabase): EarthquakeDao = db.earthquakeDao()

    @Provides
    fun provideLandslideDao(db: ShelterDatabase): LandslideDao = db.landslideDao()

    @Provides
    fun provideFloodDao(db: ShelterDatabase): FloodDao = db.floodDao()

    @Provides
    fun provideForestFireDao(db: ShelterDatabase): ForestFireDao = db.forestFireDao()

    @Provides
    fun provideWeatherForecastDao(db: ShelterDatabase): WeatherForecastDao = db.weatherForecastDao()

    @Provides
    fun provideSyncMetaDao(db: ShelterDatabase): SyncMetaDao = db.syncMetaDao()
}
