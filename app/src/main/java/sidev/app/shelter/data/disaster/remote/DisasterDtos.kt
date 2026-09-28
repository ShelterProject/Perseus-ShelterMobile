package sidev.app.shelter.data.disaster.remote

import kotlinx.serialization.Serializable

// Field names match app_v2/docs/DATA_CONTRACT.md and the assets/predictions/*.json seed files.

@Serializable
data class EarthquakePredictionDto(
    val location: String,
    val date: String,
    val avgMagnitude: Double,
    val latMin: Double,
    val latMax: Double,
    val lonMin: Double,
    val lonMax: Double,
    val depthMinKm: Double,
    val depthMaxKm: Double,
)

@Serializable
data class LandslidePredictionDto(
    val location: String,
    val date: String,
    val condition: String,
)

@Serializable
data class FloodPredictionDto(
    val village: String,
    val address: String,
    val lat: Double,
    val lon: Double,
    val date: String,
    val condition: String,
)

@Serializable
data class ForestFirePredictionDto(
    val date: String,
    val regions: Map<String, Int>,
)
