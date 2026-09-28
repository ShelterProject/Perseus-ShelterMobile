package sidev.app.shelter.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import sidev.app.shelter.data.auth.AuthRepositoryImpl
import sidev.app.shelter.data.disaster.DisasterRepositoryImpl
import sidev.app.shelter.data.weather.WeatherRepositoryImpl
import sidev.app.shelter.domain.repository.AuthRepository
import sidev.app.shelter.domain.repository.DisasterRepository
import sidev.app.shelter.domain.repository.WeatherRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDisasterRepository(impl: DisasterRepositoryImpl): DisasterRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository
}
