package sidev.app.shelter.core.result

/**
 * Progress of the weekly/on-demand prediction data sync (GitHub raw -> Room).
 * Observed by [sidev.app.shelter.feature.sync.SyncOverlay] to block the UI while a sync runs.
 */
sealed interface SyncState {
    data object Idle : SyncState
    data class Syncing(
        val step: String,
        val processedRows: Int,
        val totalRows: Int,
    ) : SyncState
    data object Done : SyncState
    data class Error(val message: String) : SyncState
}
