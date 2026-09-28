package sidev.app.shelter.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sidev.app.shelter.data.sync.SyncEngine
import sidev.app.shelter.domain.model.EarthquakePrediction
import sidev.app.shelter.domain.model.FloodPrediction
import sidev.app.shelter.domain.model.ForestFirePrediction
import sidev.app.shelter.domain.model.LandslidePrediction
import sidev.app.shelter.domain.model.WeatherForecast
import sidev.app.shelter.domain.repository.DisasterRepository
import sidev.app.shelter.domain.repository.WeatherRepository
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
