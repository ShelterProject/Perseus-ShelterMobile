package id.my.shelter.app.data.sync

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert

/** One row per prediction dataset (earthquake/landslide/flood/forest_fire/weather). */
@Entity(tableName = "sync_meta")
data class SyncMetaEntity(
    @PrimaryKey val datasetKey: String,
    val lastSyncedAtEpochMillis: Long,
)

@Dao
interface SyncMetaDao {
    @Query("SELECT * FROM sync_meta WHERE datasetKey = :key")
    suspend fun get(key: String): SyncMetaEntity?

    @Upsert
    suspend fun upsert(entity: SyncMetaEntity)
}
