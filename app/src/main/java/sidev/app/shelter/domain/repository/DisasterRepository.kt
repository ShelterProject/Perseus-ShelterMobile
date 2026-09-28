package sidev.app.shelter.domain.repository

import kotlinx.coroutines.flow.Flow
import sidev.app.shelter.domain.model.EarthquakePrediction
import sidev.app.shelter.domain.model.FloodPrediction
import sidev.app.shelter.domain.model.ForestFirePrediction
import sidev.app.shelter.domain.model.LandslidePrediction

/**
 * Room is the only read path (see docs/DATA_CONTRACT.md); [sync] is what refreshes it from
 * GitHub raw and is driven by [sidev.app.shelter.data.sync.SyncEngine], not by these getters.
 */
interface DisasterRepository {
    fun earthquakePredictions(): Flow<List<EarthquakePrediction>>
    fun landslidePredictions(): Flow<List<LandslidePrediction>>
    fun floodPredictions(): Flow<List<FloodPrediction>>
    fun forestFirePredictions(): Flow<List<ForestFirePrediction>>
}
