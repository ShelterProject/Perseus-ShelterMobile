package id.my.shelter.app.data.disaster.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EarthquakeDao {
    @Query("SELECT * FROM earthquake_predictions ORDER BY date DESC")
    fun observeAll(): Flow<List<EarthquakeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<EarthquakeEntity>)

    @Query("DELETE FROM earthquake_predictions")
    suspend fun clear()
}

@Dao
interface LandslideDao {
    @Query("SELECT * FROM landslide_predictions ORDER BY date DESC")
    fun observeAll(): Flow<List<LandslideEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<LandslideEntity>)

    @Query("DELETE FROM landslide_predictions")
    suspend fun clear()
}

@Dao
interface FloodDao {
    @Query("SELECT * FROM flood_predictions ORDER BY date DESC")
    fun observeAll(): Flow<List<FloodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FloodEntity>)

    @Query("DELETE FROM flood_predictions")
    suspend fun clear()
}

@Dao
interface ForestFireDao {
    @Query("SELECT * FROM forest_fire_predictions ORDER BY date DESC")
    fun observeAll(): Flow<List<ForestFireEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ForestFireEntity>)

    @Query("DELETE FROM forest_fire_predictions")
    suspend fun clear()
}
