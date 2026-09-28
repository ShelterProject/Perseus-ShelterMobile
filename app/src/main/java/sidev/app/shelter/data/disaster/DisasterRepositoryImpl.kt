package sidev.app.shelter.data.disaster

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sidev.app.shelter.data.disaster.local.EarthquakeDao
import sidev.app.shelter.data.disaster.local.FloodDao
import sidev.app.shelter.data.disaster.local.ForestFireDao
import sidev.app.shelter.data.disaster.local.LandslideDao
import sidev.app.shelter.domain.model.EarthquakePrediction
import sidev.app.shelter.domain.model.FloodPrediction
import sidev.app.shelter.domain.model.ForestFirePrediction
import sidev.app.shelter.domain.model.LandslidePrediction
import sidev.app.shelter.domain.repository.DisasterRepository
import javax.inject.Inject
import javax.inject.Singleton

/** Room-only: nothing here ever hits the network. Refreshing Room is [sidev.app.shelter.data.sync.SyncEngine]'s job. */
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
