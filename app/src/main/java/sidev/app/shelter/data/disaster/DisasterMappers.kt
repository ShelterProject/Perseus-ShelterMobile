package sidev.app.shelter.data.disaster

import kotlinx.serialization.json.Json
import sidev.app.shelter.data.disaster.local.EarthquakeEntity
import sidev.app.shelter.data.disaster.local.FloodEntity
import sidev.app.shelter.data.disaster.local.ForestFireEntity
import sidev.app.shelter.data.disaster.local.LandslideEntity
import sidev.app.shelter.data.disaster.remote.EarthquakePredictionDto
import sidev.app.shelter.data.disaster.remote.FloodPredictionDto
import sidev.app.shelter.data.disaster.remote.ForestFirePredictionDto
import sidev.app.shelter.data.disaster.remote.LandslidePredictionDto
import sidev.app.shelter.domain.model.EarthquakePrediction
import sidev.app.shelter.domain.model.FloodPrediction
import sidev.app.shelter.domain.model.ForestFirePrediction
import sidev.app.shelter.domain.model.LandslidePrediction
import java.time.LocalDate

private val regionsJson = Json { ignoreUnknownKeys = true }

fun EarthquakePredictionDto.toEntity() = EarthquakeEntity(
    date = date,
    location = location,
    avgMagnitude = avgMagnitude,
    latMin = latMin,
    latMax = latMax,
    lonMin = lonMin,
    lonMax = lonMax,
    depthMinKm = depthMinKm,
    depthMaxKm = depthMaxKm,
)

fun EarthquakeEntity.toDomain() = EarthquakePrediction(
    date = LocalDate.parse(date),
    location = location,
    avgMagnitude = avgMagnitude,
    latRange = latMin..latMax,
    lonRange = lonMin..lonMax,
    depthRangeKm = depthMinKm..depthMaxKm,
)

fun LandslidePredictionDto.toEntity() = LandslideEntity(date = date, location = location, condition = condition)

fun LandslideEntity.toDomain() = LandslidePrediction(date = LocalDate.parse(date), location = location, condition = condition)

fun FloodPredictionDto.toEntity() = FloodEntity(
    date = date,
    village = village,
    address = address,
    latitude = lat,
    longitude = lon,
    condition = condition,
)

fun FloodEntity.toDomain() = FloodPrediction(
    date = LocalDate.parse(date),
    village = village,
    address = address,
    latitude = latitude,
    longitude = longitude,
    condition = condition,
)

fun ForestFirePredictionDto.toEntity() = ForestFireEntity(
    date = date,
    regionRiskJson = regionsJson.encodeToString(regions),
)

fun ForestFireEntity.toDomain() = ForestFirePrediction(
    date = LocalDate.parse(date),
    regionRiskPercentage = regionsJson.decodeFromString<Map<String, Int>>(regionRiskJson),
)
