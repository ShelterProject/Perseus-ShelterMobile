package id.my.shelter.app.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import id.my.shelter.app.core.result.Resource
import id.my.shelter.app.domain.model.Gender
import id.my.shelter.app.domain.repository.AuthRepository
import javax.inject.Inject

data class AuthUiState(
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    val currentUser = authRepository.currentUser
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, null)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        _uiState.value = AuthUiState(isSubmitting = true)
        viewModelScope.launch {
            when (val result = authRepository.signIn(email, password)) {
                is Resource.Success -> {
                    _uiState.value = AuthUiState(isSubmitting = false)
                    onSuccess()
                }
                is Resource.Error -> _uiState.value = AuthUiState(errorMessage = result.message)
                Resource.Loading -> Unit
            }
        }
    }

    fun signUp(
        email: String,
        password: String,
        fullName: String,
        gender: Gender,
        onSuccess: () -> Unit,
    ) {
        _uiState.value = AuthUiState(isSubmitting = true)
        viewModelScope.launch {
            when (val result = authRepository.signUp(email, password, fullName, gender)) {
                is Resource.Success -> {
                    _uiState.value = AuthUiState(isSubmitting = false)
                    onSuccess()
                }
                is Resource.Error -> _uiState.value = AuthUiState(errorMessage = result.message)
                Resource.Loading -> Unit
            }
        }
    }

    fun signInWithGoogle(googleIdToken: String, onSuccess: () -> Unit) {
        _uiState.value = AuthUiState(isSubmitting = true)
        viewModelScope.launch {
            when (val result = authRepository.signInWithGoogle(googleIdToken)) {
                is Resource.Success -> {
                    _uiState.value = AuthUiState(isSubmitting = false)
                    onSuccess()
                }
                is Resource.Error -> _uiState.value = AuthUiState(errorMessage = result.message)
                Resource.Loading -> Unit
            }
        }
    }

    fun consumeError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /** Lets the UI surface a Credential Manager / Google sign-in failure through the same error slot. */
    fun reportError(message: String) {
        _uiState.value = AuthUiState(errorMessage = message)
    }
}
