package id.my.kulitech.nippia.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.ProfileStatsOverview
import id.my.kulitech.nippia.domain.repository.AuthRepository
import id.my.kulitech.nippia.domain.usecase.GetProfileStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LinkGuestAccountUiState(
    val isLoading: Boolean = true,
    val overview: ProfileStatsOverview? = null,
    val isLinking: Boolean = false,
    val linkedSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class LinkGuestAccountViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getProfileStatsUseCase: GetProfileStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LinkGuestAccountUiState())
    val uiState: StateFlow<LinkGuestAccountUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            when (val res = getProfileStatsUseCase()) {
                is AppResult.Success -> _uiState.update { it.copy(isLoading = false, overview = res.data) }
                is AppResult.Error -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun linkWithGoogle(idToken: String, onLinked: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLinking = true, errorMessage = null) }
            when (val res = authRepository.linkGoogleAccount(idToken)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLinking = false, linkedSuccess = true) }
                    onLinked()
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isLinking = false, errorMessage = res.message)
                }
            }
        }
    }
}
