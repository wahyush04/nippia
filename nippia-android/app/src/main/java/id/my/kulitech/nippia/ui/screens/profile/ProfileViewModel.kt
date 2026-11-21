package id.my.kulitech.nippia.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.ProfileStatsOverview
import id.my.kulitech.nippia.domain.usecase.GetProfileStatsUseCase
import id.my.kulitech.nippia.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = true,
    val overview: ProfileStatsOverview? = null,
    val isSyncing: Boolean = false,
    val actionMessage: String? = null
)

sealed interface ProfileNavEvent {
    data object ToLogin : ProfileNavEvent
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileStatsUseCase: GetProfileStatsUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _navEvents = MutableSharedFlow<ProfileNavEvent>()
    val navEvents: SharedFlow<ProfileNavEvent> = _navEvents.asSharedFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getProfileStatsUseCase()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, overview = result.data)
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onQuickSync() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            loadProfile()
            _uiState.update {
                it.copy(isSyncing = false, actionMessage = "Sinkronisasi profil & statistik selesai!")
            }
        }
    }

    fun onActionPlaceholder(message: String) {
        _uiState.update { it.copy(actionMessage = message) }
    }

    fun onDismissMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            logoutUseCase()
            _navEvents.emit(ProfileNavEvent.ToLogin)
        }
    }
}
