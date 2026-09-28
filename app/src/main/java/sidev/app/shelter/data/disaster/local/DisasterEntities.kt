package sidev.app.shelter.data.disaster.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "earthquake_predictions")
data class EarthquakeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // ISO-8601 (yyyy-MM-dd)
    val location: String,
    val avgMagnitude: Double,
    val latMin: Double,
    val latMax: Double,
    val lonMin: Double,
    val lonMax: Double,
    val depthMinKm: Double,
    val depthMaxKm: Double,
)

@Entity(tableName = "landslide_predictions")
data class LandslideEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val location: String,
    val condition: String,
)

@Entity(tableName = "flood_predictions")
data class FloodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val village: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val condition: String,
)

@Entity(tableName = "forest_fire_predictions")
data class ForestFireEntity(
    @PrimaryKey val date: String, // one row per date already, matches the source shape
    val regionRiskJson: String, // serialized Map<String, Int>, decoded by the mapper
)
