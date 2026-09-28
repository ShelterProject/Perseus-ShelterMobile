package id.my.shelter.app.data.sync

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import id.my.shelter.app.core.result.SyncState
import id.my.shelter.app.data.disaster.local.EarthquakeDao
import id.my.shelter.app.data.disaster.local.FloodDao
import id.my.shelter.app.data.disaster.local.ForestFireDao
import id.my.shelter.app.data.disaster.local.LandslideDao
import id.my.shelter.app.data.disaster.remote.EarthquakePredictionDto
import id.my.shelter.app.data.disaster.remote.FloodPredictionDto
import id.my.shelter.app.data.disaster.remote.ForestFirePredictionDto
import id.my.shelter.app.data.disaster.remote.LandslidePredictionDto
import id.my.shelter.app.data.disaster.toEntity
import id.my.shelter.app.data.weather.local.WeatherForecastDao
import id.my.shelter.app.data.weather.remote.WeatherForecastDto
import id.my.shelter.app.data.weather.toEntity
import javax.inject.Inject
import javax.inject.Singleton

private const val CHUNK_SIZE = 500
private const val MAX_CACHE_AGE_MILLIS = 7L * 24 * 60 * 60 * 1000 // 7 days

/**
 * Parses each predictions/*.json file and writes it into Room in [CHUNK_SIZE]-row batches
 * (not one giant transaction), publishing progress to [SyncStatusRepository] as it goes so
 * [id.my.shelter.app.feature.sync.SyncOverlay] can show a real progress bar.
 *
 * Source per dataset: GitHub raw first; if that fails and the dataset was never synced before,
 * fall back to the bundled asset seed so Room isn't empty on a first, offline launch.
 */
@Singleton
class SyncEngine @Inject constructor(
    private val githubDataSource: GithubPredictionDataSource,
    private val assetDataSource: AssetPredictionDataSource,
    private val syncMetaDao: SyncMetaDao,
    private val syncStatusRepository: SyncStatusRepository,
    private val earthquakeDao: EarthquakeDao,
    private val landslideDao: LandslideDao,
    private val floodDao: FloodDao,
    private val forestFireDao: ForestFireDao,
    private val weatherForecastDao: WeatherForecastDao,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun shouldSync(): Boolean {
        val meta = syncMetaDao.get(DATASET_EARTHQUAKE)
        return meta == null || System.currentTimeMillis() - meta.lastSyncedAtEpochMillis > MAX_CACHE_AGE_MILLIS
    }

    suspend fun syncAll() = withContext(Dispatchers.IO) {
        syncStatusRepository.update(SyncState.Syncing("Memulai sinkronisasi...", 0, 0))
        try {
            syncDataset(DATASET_EARTHQUAKE, "gempa", EarthquakePredictionDto.serializer()) { dtos ->
                earthquakeDao.insertAll(dtos.map { it.toEntity() })
            }
            syncDataset(DATASET_LANDSLIDE, "longsor", LandslidePredictionDto.serializer()) { dtos ->
                landslideDao.insertAll(dtos.map { it.toEntity() })
            }
            syncDataset(DATASET_FLOOD, "banjir", FloodPredictionDto.serializer()) { dtos ->
                floodDao.insertAll(dtos.map { it.toEntity() })
            }
            syncDataset(DATASET_FOREST_FIRE, "karhutla", ForestFirePredictionDto.serializer()) { dtos ->
                forestFireDao.insertAll(dtos.map { it.toEntity() })
            }
            syncDataset(DATASET_WEATHER, "cuaca", WeatherForecastDto.serializer()) { dtos ->
                weatherForecastDao.insertAll(dtos.map { it.toEntity() })
            }
            syncStatusRepository.update(SyncState.Done)
        } catch (e: Exception) {
            syncStatusRepository.update(SyncState.Error(e.message ?: "Sinkronisasi gagal"))
        }
    }

    private suspend fun <T> syncDataset(
        key: String,
        label: String,
        serializer: kotlinx.serialization.KSerializer<T>,
        insertChunk: suspend (List<T>) -> Unit,
    ) {
        val fileName = "$key.json"
        val alreadySyncedBefore = syncMetaDao.get(key) != null

        val rawJson = runCatching { githubDataSource.fetchJson(fileName) }
            .getOrElse { githubError ->
                if (alreadySyncedBefore) {
                    // Keep whatever Room already has; this dataset just isn't refreshed this round.
                    return
                }
                assetDataSource.readJson(fileName)
            }

        val items = json.decodeFromString(kotlinx.serialization.builtins.ListSerializer(serializer), rawJson)
        val total = items.size
        items.chunked(CHUNK_SIZE).forEachIndexed { index, chunk ->
            insertChunk(chunk)
            val processed = minOf((index + 1) * CHUNK_SIZE, total)
            syncStatusRepository.update(
                SyncState.Syncing(step = "Memperbarui data $label...", processedRows = processed, totalRows = total),
            )
        }
        syncMetaDao.upsert(SyncMetaEntity(datasetKey = key, lastSyncedAtEpochMillis = System.currentTimeMillis()))
    }

    companion object {
        const val DATASET_EARTHQUAKE = "earthquake"
        const val DATASET_LANDSLIDE = "landslide"
        const val DATASET_FLOOD = "flood"
        const val DATASET_FOREST_FIRE = "forest_fire"
        const val DATASET_WEATHER = "weather"
    }
}
