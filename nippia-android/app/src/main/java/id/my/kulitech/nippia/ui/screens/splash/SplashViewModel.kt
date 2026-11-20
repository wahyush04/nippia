package id.my.kulitech.nippia.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.domain.model.SessionState
import id.my.kulitech.nippia.domain.usecase.CheckSessionUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashUiState(
    val isCheckingSession: Boolean = true,
    val isReadyToStart: Boolean = false
)

sealed interface SplashNavEvent {
    data object ToLogin : SplashNavEvent
    data object ToOnboarding : SplashNavEvent
    data object ToHome : SplashNavEvent
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkSessionUseCase: CheckSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private val _navEvents = MutableSharedFlow<SplashNavEvent>()
    val navEvents: SharedFlow<SplashNavEvent> = _navEvents.asSharedFlow()

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            delay(800)
            when (val session = checkSessionUseCase()) {
                is SessionState.Authenticated -> {
                    _uiState.update { it.copy(isCheckingSession = false) }
                    if (session.user.onboardingCompleted) {
                        _navEvents.emit(SplashNavEvent.ToHome)
                    } else {
                        _navEvents.emit(SplashNavEvent.ToOnboarding)
                    }
                }
                SessionState.Unauthenticated -> {
                    _uiState.update {
                        it.copy(isCheckingSession = false, isReadyToStart = true)
                    }
                }
            }
        }
    }

    fun onStartLearningClick() {
        viewModelScope.launch {
            _navEvents.emit(SplashNavEvent.ToLogin)
        }
    }
}
