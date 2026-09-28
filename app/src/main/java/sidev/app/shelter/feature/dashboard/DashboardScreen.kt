package sidev.app.shelter.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")

private data class DisasterCardData(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val subtitle: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    val cards = listOf(
        DisasterCardData(
            title = "Gempa Bumi",
            icon = Icons.Filled.Vibration,
            subtitle = uiState.latestEarthquake?.let {
                "${it.location} · magnitudo ~${"%.1f".format(it.avgMagnitude)} · ${it.date.format(dateFormatter)}"
            } ?: "Belum ada data",
        ),
        DisasterCardData(
            title = "Tanah Longsor",
            icon = Icons.Filled.Terrain,
            subtitle = uiState.latestLandslide?.let {
                "${it.location} · ${it.condition} · ${it.date.format(dateFormatter)}"
            } ?: "Belum ada data",
        ),
        DisasterCardData(
            title = "Banjir",
            icon = Icons.Filled.Water,
            subtitle = uiState.latestFlood?.let {
                "${it.village} · ${it.condition} · ${it.date.format(dateFormatter)}"
            } ?: "Belum ada data",
        ),
        DisasterCardData(
            title = "Karhutla",
            icon = Icons.Filled.LocalFireDepartment,
            subtitle = uiState.latestForestFire?.let { prediction ->
                val riskiest = prediction.regionRiskPercentage.maxByOrNull { it.value }
                riskiest?.let { "${it.key} risiko ${it.value}% · ${prediction.date.format(dateFormatter)}" }
                    ?: prediction.date.format(dateFormatter)
            } ?: "Belum ada data",
        ),
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Shelter") }) },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp, padding.calculateTopPadding() + 8.dp, 16.dp, 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { WeatherSummaryCard(uiState) }
            items(cards) { card -> DisasterCard(card) }
        }
    }
}

@Composable
private fun WeatherSummaryCard(uiState: DashboardUiState) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Prakiraan Cuaca", style = MaterialTheme.typography.titleLarge)
            val weather = uiState.latestWeather
            Text(
                text = weather?.let {
                    "${it.condition} · ${"%.1f".format(it.temperature)}°C · kelembapan ${"%.0f".format(it.humidity)}% · ${it.date.format(dateFormatter)}"
                } ?: "Belum ada data",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun DisasterCard(data: DisasterCardData) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = data.icon, contentDescription = data.title, tint = MaterialTheme.colorScheme.primary)
            Text(text = data.title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 8.dp))
            Text(text = data.subtitle, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
