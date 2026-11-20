package id.my.kulitech.nippia.feature.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.auth.GoogleSignInClient
import id.my.kulitech.nippia.core.network.auth.AuthResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val session: AuthResponseDto? = null,
    val errorMessage: String? = null,
    val waveTriggerCount: Int = 0,
    val celebrateTriggerCount: Int = 0,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val googleSignInClient: GoogleSignInClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState(waveTriggerCount = 1))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signInWithGoogle(activityContext: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { googleSignInClient.requestGoogleIdToken(activityContext) }
                .fold(
                    onSuccess = { idToken ->
                        authRepository.loginWithGoogle(idToken).fold(
                            onSuccess = { session ->
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        session = session,
                                        celebrateTriggerCount = it.celebrateTriggerCount + 1,
                                    )
                                }
                            },
                            onFailure = { err ->
                                _uiState.update {
                                    it.copy(isLoading = false, errorMessage = err.message)
                                }
                            },
                        )
                    },
                    onFailure = { err ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = err.message ?: "Google Sign-In dibatalkan.",
                            )
                        }
                    },
                )
        }
    }

    fun signInAsGuest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.loginAsGuest().fold(
                onSuccess = { session ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            session = session,
                            celebrateTriggerCount = it.celebrateTriggerCount + 1,
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = err.message)
                    }
                },
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update {
                it.copy(session = null, errorMessage = null, waveTriggerCount = it.waveTriggerCount + 1)
            }
        }
    }
}
