package id.my.shelter.app.domain.model

import java.time.LocalDate

enum class DisasterType { EARTHQUAKE, LANDSLIDE, FLOOD, FOREST_FIRE }

/**
 * Base of the disaster-prediction domain model. Subtypes mirror the shape of the ML pipeline's
 * raw output (see _res/from_CC/*.json and Shelter_Cloud predict/*.json) but drop fields the app
 * never uses (e.g. earthquake's raw lat/lon percentile columns are kept as ranges, not each stat).
 */
sealed class DisasterPrediction {
    abstract val date: LocalDate
    abstract val type: DisasterType
}

data class EarthquakePrediction(
    override val date: LocalDate,
    val location: String,
    val avgMagnitude: Double,
    val latRange: ClosedFloatingPointRange<Double>,
    val lonRange: ClosedFloatingPointRange<Double>,
    val depthRangeKm: ClosedFloatingPointRange<Double>,
) : DisasterPrediction() {
    override val type = DisasterType.EARTHQUAKE
}

data class LandslidePrediction(
    override val date: LocalDate,
    val location: String,
    val condition: String,
) : DisasterPrediction() {
    override val type = DisasterType.LANDSLIDE
}

data class FloodPrediction(
    override val date: LocalDate,
    val village: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val condition: String,
) : DisasterPrediction() {
    override val type = DisasterType.FLOOD
}

/**
 * One row per date; [regionRiskPercentage] maps a kabupaten/kota name to its 0-100 risk score,
 * matching karhutla_from_server.json's shape (a column per region) instead of one row per region.
 */
data class ForestFirePrediction(
    override val date: LocalDate,
    val regionRiskPercentage: Map<String, Int>,
) : DisasterPrediction() {
    override val type = DisasterType.FOREST_FIRE
}
