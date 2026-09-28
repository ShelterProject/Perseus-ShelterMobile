package id.my.shelter.app.data.disaster

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import id.my.shelter.app.data.disaster.local.EarthquakeDao
import id.my.shelter.app.data.disaster.local.FloodDao
import id.my.shelter.app.data.disaster.local.ForestFireDao
import id.my.shelter.app.data.disaster.local.LandslideDao
import id.my.shelter.app.domain.model.EarthquakePrediction
import id.my.shelter.app.domain.model.FloodPrediction
import id.my.shelter.app.domain.model.ForestFirePrediction
import id.my.shelter.app.domain.model.LandslidePrediction
import id.my.shelter.app.domain.repository.DisasterRepository
import javax.inject.Inject
import javax.inject.Singleton

/** Room-only: nothing here ever hits the network. Refreshing Room is [id.my.shelter.app.data.sync.SyncEngine]'s job. */
@Singleton
class DisasterRepositoryImpl @Inject constructor(
    private val earthquakeDao: EarthquakeDao,
    private val landslideDao: LandslideDao,
    private val floodDao: FloodDao,
    private val forestFireDao: ForestFireDao,
) : DisasterRepository {

    override fun earthquakePredictions(): Flow<List<EarthquakePrediction>> =
        earthquakeDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun landslidePredictions(): Flow<List<LandslidePrediction>> =
        landslideDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun floodPredictions(): Flow<List<FloodPrediction>> =
        floodDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun forestFirePredictions(): Flow<List<ForestFirePrediction>> =
        forestFireDao.observeAll().map { list -> list.map { it.toDomain() } }
}
