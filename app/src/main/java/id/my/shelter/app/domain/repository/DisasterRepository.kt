package id.my.shelter.app.domain.repository

import kotlinx.coroutines.flow.Flow
import id.my.shelter.app.domain.model.EarthquakePrediction
import id.my.shelter.app.domain.model.FloodPrediction
import id.my.shelter.app.domain.model.ForestFirePrediction
import id.my.shelter.app.domain.model.LandslidePrediction

/**
 * Room is the only read path (see docs/DATA_CONTRACT.md); [sync] is what refreshes it from
 * GitHub raw and is driven by [id.my.shelter.app.data.sync.SyncEngine], not by these getters.
 */
interface DisasterRepository {
    fun earthquakePredictions(): Flow<List<EarthquakePrediction>>
    fun landslidePredictions(): Flow<List<LandslidePrediction>>
    fun floodPredictions(): Flow<List<FloodPrediction>>
    fun forestFirePredictions(): Flow<List<ForestFirePrediction>>
}
