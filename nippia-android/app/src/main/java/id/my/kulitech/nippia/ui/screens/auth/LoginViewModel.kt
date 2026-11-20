package id.my.kulitech.nippia.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.AuthSession
import id.my.kulitech.nippia.domain.usecase.LoginAsGuestUseCase
import id.my.kulitech.nippia.domain.usecase.LoginWithGoogleUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoadingGoogle: Boolean = false,
    val isLoadingGuest: Boolean = false,
    val errorMessage: String? = null
) {
    val isBusy: Boolean get() = isLoadingGoogle || isLoadingGuest
}

sealed interface LoginNavEvent {
    data class ToAuthSuccess(val session: AuthSession) : LoginNavEvent
    data object ToOnboarding : LoginNavEvent
    data object ToHome : LoginNavEvent
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val loginAsGuestUseCase: LoginAsGuestUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navEvents = MutableSharedFlow<LoginNavEvent>()
    val navEvents: SharedFlow<LoginNavEvent> = _navEvents.asSharedFlow()

    fun onGoogleIdTokenReceived(idToken: String) {
        if (_uiState.value.isBusy) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingGoogle = true, errorMessage = null) }
            when (val result = loginWithGoogleUseCase(idToken = idToken)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoadingGoogle = false) }
                    val session = result.data
                    if (session.isNewUser || session.welcomeBonus != null) {
                        _navEvents.emit(LoginNavEvent.ToAuthSuccess(session))
                    } else if (session.user.onboardingCompleted) {
                        _navEvents.emit(LoginNavEvent.ToHome)
                    } else {
                        _navEvents.emit(LoginNavEvent.ToOnboarding)
                    }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(isLoadingGoogle = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun onGoogleSignInError(message: String) {
        _uiState.update { it.copy(isLoadingGoogle = false, errorMessage = message) }
    }

    fun onGuestLoginClick() {
        if (_uiState.value.isBusy) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingGuest = true, errorMessage = null) }
            when (val result = loginAsGuestUseCase()) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoadingGuest = false) }
                    if (result.data.user.onboardingCompleted) {
                        _navEvents.emit(LoginNavEvent.ToHome)
                    } else {
                        _navEvents.emit(LoginNavEvent.ToOnboarding)
                    }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(isLoadingGuest = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
