package sidev.app.shelter.feature.sync

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import sidev.app.shelter.data.sync.SyncStatusRepository
import javax.inject.Inject

@HiltViewModel
class SyncOverlayViewModel @Inject constructor(
    syncStatusRepository: SyncStatusRepository,
) : ViewModel() {
    val syncState = syncStatusRepository.state
}
