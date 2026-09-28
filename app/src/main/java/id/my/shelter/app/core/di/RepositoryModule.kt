package id.my.shelter.app.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.my.shelter.app.data.auth.AuthRepositoryImpl
import id.my.shelter.app.data.disaster.DisasterRepositoryImpl
import id.my.shelter.app.data.weather.WeatherRepositoryImpl
import id.my.shelter.app.domain.repository.AuthRepository
import id.my.shelter.app.domain.repository.DisasterRepository
import id.my.shelter.app.domain.repository.WeatherRepository
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
