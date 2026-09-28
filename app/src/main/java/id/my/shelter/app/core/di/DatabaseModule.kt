package id.my.shelter.app.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.my.shelter.app.data.disaster.local.EarthquakeDao
import id.my.shelter.app.data.disaster.local.FloodDao
import id.my.shelter.app.data.disaster.local.ForestFireDao
import id.my.shelter.app.data.disaster.local.LandslideDao
import id.my.shelter.app.data.sync.SyncMetaDao
import id.my.shelter.app.data.weather.local.WeatherForecastDao
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
