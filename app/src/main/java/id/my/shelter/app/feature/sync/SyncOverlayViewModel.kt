package id.my.shelter.app.feature.sync

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.shelter.app.data.sync.SyncStatusRepository
import javax.inject.Inject

@HiltViewModel
class SyncOverlayViewModel @Inject constructor(
    syncStatusRepository: SyncStatusRepository,
) : ViewModel() {
    val syncState = syncStatusRepository.state
}
