package id.my.shelter.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import id.my.shelter.app.core.ui.theme.ShelterTheme
import id.my.shelter.app.feature.navigation.ShelterNavHost
import id.my.shelter.app.feature.sync.SyncOverlay

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
