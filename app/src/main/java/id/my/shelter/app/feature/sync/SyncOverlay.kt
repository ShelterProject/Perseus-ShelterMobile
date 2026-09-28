package id.my.shelter.app.feature.sync

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.shelter.app.core.result.SyncState

/**
 * Non-dismissable overlay shown at the app root whenever a prediction data sync is running
 * (weekly automatic, or the opportunistic check in DashboardViewModel). The user cannot back out,
 * tap outside, or navigate away mid-sync — per product requirement, syncing must not be interrupted.
 */
@Composable
fun SyncOverlay(viewModel: SyncOverlayViewModel = hiltViewModel()) {
    val state by viewModel.syncState.collectAsState()
    val syncing = state as? SyncState.Syncing ?: return

    Dialog(
        onDismissRequest = { /* intentionally no-op: sync cannot be interrupted */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    ) {
        Surface(shape = MaterialTheme.shapes.large, tonalElevation = 6.dp) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Text(text = "Memperbarui data", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = syncing.step,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
                )
                val progress = if (syncing.totalRows > 0) {
                    syncing.processedRows / syncing.totalRows.toFloat()
                } else {
                    0f
                }
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                if (syncing.totalRows > 0) {
                    Text(
                        text = "${syncing.processedRows}/${syncing.totalRows}",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
    }
}
