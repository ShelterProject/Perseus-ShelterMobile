package id.my.shelter.app.data.disaster

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test
import id.my.shelter.app.data.disaster.remote.EarthquakePredictionDto
import id.my.shelter.app.data.disaster.remote.ForestFirePredictionDto
import id.my.shelter.app.data.disaster.remote.LandslidePredictionDto

/**
 * Verifies DTO -> Entity -> domain mapping against the field names used in
 * app_v2/docs/DATA_CONTRACT.md / assets/predictions/*.json, without touching Firebase, Room, or
 * the network — this is what the plan's "Verifikasi" step meant by testing mappers offline.
 */
class DisasterMapperTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `earthquake dto maps to domain with correct ranges`() {
        val dto = json.decodeFromString(
            EarthquakePredictionDto.serializer(),
            """{"location":"Pulo Batal","date":"2021-01-01","avgMagnitude":3.45,
                "latMin":-0.40,"latMax":4.50,"lonMin":94.65,"lonMax":99.33,
                "depthMinKm":-87.93,"depthMaxKm":131.93}""",
        )
        val domain = dto.toEntity().toDomain()

        assertEquals("Pulo Batal", domain.location)
        assertEquals(3.45, domain.avgMagnitude, 0.001)
        assertEquals(-0.40..4.50, domain.latRange)
    }

    @Test
    fun `landslide dto round trips condition`() {
        val dto = json.decodeFromString(
            LandslidePredictionDto.serializer(),
            """{"location":"Sungai Korang","date":"2021-01-01","condition":"Agak Rawan"}""",
        )
        val domain = dto.toEntity().toDomain()

        assertEquals("Agak Rawan", domain.condition)
    }

    @Test
    fun `forest fire dto keeps per-region risk map, one row per date`() {
        val dto = json.decodeFromString(
            ForestFirePredictionDto.serializer(),
            """{"date":"2021-01-11","regions":{"Asahan":83,"Dairi":48}}""",
        )
        val domain = dto.toEntity().toDomain()

        assertEquals(83, domain.regionRiskPercentage["Asahan"])
        assertEquals(48, domain.regionRiskPercentage["Dairi"])
    }
}
