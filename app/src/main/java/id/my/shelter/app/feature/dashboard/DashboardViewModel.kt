package id.my.shelter.app.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import id.my.shelter.app.data.sync.SyncEngine
import id.my.shelter.app.domain.model.EarthquakePrediction
import id.my.shelter.app.domain.model.FloodPrediction
import id.my.shelter.app.domain.model.ForestFirePrediction
import id.my.shelter.app.domain.model.LandslidePrediction
import id.my.shelter.app.domain.model.WeatherForecast
import id.my.shelter.app.domain.repository.DisasterRepository
import id.my.shelter.app.domain.repository.WeatherRepository
import javax.inject.Inject

data class DashboardUiState(
    val latestEarthquake: EarthquakePrediction? = null,
    val latestLandslide: LandslidePrediction? = null,
    val latestFlood: FloodPrediction? = null,
    val latestForestFire: ForestFirePrediction? = null,
    val latestWeather: WeatherForecast? = null,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    disasterRepository: DisasterRepository,
    weatherRepository: WeatherRepository,
    private val syncEngine: SyncEngine,
) : ViewModel() {

    val uiState = combine(
        disasterRepository.earthquakePredictions(),
        disasterRepository.landslidePredictions(),
        disasterRepository.floodPredictions(),
        disasterRepository.forestFirePredictions(),
        weatherRepository.forecasts(),
    ) { earthquakes, landslides, floods, forestFires, forecasts ->
        DashboardUiState(
            latestEarthquake = earthquakes.maxByOrNull { it.date },
            latestLandslide = landslides.maxByOrNull { it.date },
            latestFlood = floods.maxByOrNull { it.date },
            latestForestFire = forestFires.maxByOrNull { it.date },
            latestWeather = forecasts.maxByOrNull { it.date },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    init {
        viewModelScope.launch {
            if (syncEngine.shouldSync()) {
                syncEngine.syncAll()
            }
        }
    }
}
