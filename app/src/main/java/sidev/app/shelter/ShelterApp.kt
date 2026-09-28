package sidev.app.shelter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import sidev.app.shelter.core.ui.theme.ShelterTheme
import sidev.app.shelter.feature.navigation.ShelterNavHost
import sidev.app.shelter.feature.sync.SyncOverlay

/**
 * App root: NavHost plus the sync overlay layered on top so a running sync blocks every
 * screen, not just Dashboard (see docs/DATA_CONTRACT.md, "Strategi Penyimpanan Data & Biaya").
 */
@Composable
fun ShelterApp() {
    ShelterTheme {
        Surface(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
            Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                ShelterNavHost()
                SyncOverlay()
            }
        }
    }
}
