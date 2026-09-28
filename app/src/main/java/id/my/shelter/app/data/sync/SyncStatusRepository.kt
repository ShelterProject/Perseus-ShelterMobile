package id.my.shelter.app.data.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import id.my.shelter.app.core.result.SyncState
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for sync progress, observed by [id.my.shelter.app.feature.sync.SyncOverlay]
 * at the app root. Using a Hilt singleton instead of WorkManager's own Data payload because progress
 * needs to be reported continuously and isn't bounded by WorkManager's small Data size limit.
 */
@Singleton
class SyncStatusRepository @Inject constructor() {
    private val _state = MutableStateFlow<SyncState>(SyncState.Idle)
    val state: StateFlow<SyncState> = _state.asStateFlow()

    fun update(state: SyncState) {
        _state.value = state
    }
}
