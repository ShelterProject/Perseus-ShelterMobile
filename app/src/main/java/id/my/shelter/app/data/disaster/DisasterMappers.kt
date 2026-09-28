package id.my.shelter.app.data.disaster

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import id.my.shelter.app.data.disaster.local.EarthquakeEntity
import id.my.shelter.app.data.disaster.local.FloodEntity
import id.my.shelter.app.data.disaster.local.ForestFireEntity
import id.my.shelter.app.data.disaster.local.LandslideEntity
import id.my.shelter.app.data.disaster.remote.EarthquakePredictionDto
import id.my.shelter.app.data.disaster.remote.FloodPredictionDto
import id.my.shelter.app.data.disaster.remote.ForestFirePredictionDto
import id.my.shelter.app.data.disaster.remote.LandslidePredictionDto
import id.my.shelter.app.domain.model.EarthquakePrediction
import id.my.shelter.app.domain.model.FloodPrediction
import id.my.shelter.app.domain.model.ForestFirePrediction
import id.my.shelter.app.domain.model.LandslidePrediction
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
