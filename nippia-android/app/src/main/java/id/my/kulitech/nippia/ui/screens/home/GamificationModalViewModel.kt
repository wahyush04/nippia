package id.my.kulitech.nippia.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.GamificationDetailStatus
import id.my.kulitech.nippia.domain.repository.LearningGamificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GamificationModalUiState(
    val showStreakModal: Boolean = false,
    val showZeroHeartsModal: Boolean = false,
    val status: GamificationDetailStatus = GamificationDetailStatus(),
    val isBusy: Boolean = false,
    val feedbackMessage: String? = null
)

@HiltViewModel
class GamificationModalViewModel @Inject constructor(
    private val repository: LearningGamificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GamificationModalUiState())
    val uiState: StateFlow<GamificationModalUiState> = _uiState.asStateFlow()

    fun openStreakModal() {
        _uiState.update { it.copy(showStreakModal = true, feedbackMessage = null) }
        refreshStatus()
    }

    fun closeStreakModal() {
        _uiState.update { it.copy(showStreakModal = false, feedbackMessage = null) }
    }

    fun openZeroHeartsModal() {
        _uiState.update { it.copy(showZeroHeartsModal = true, feedbackMessage = null) }
        refreshStatus()
    }

    fun closeZeroHeartsModal() {
        _uiState.update { it.copy(showZeroHeartsModal = false, feedbackMessage = null) }
    }

    private fun refreshStatus() {
        viewModelScope.launch {
            when (val res = repository.getGamificationStatus()) {
                is AppResult.Success -> _uiState.update { it.copy(status = res.data) }
                is AppResult.Error -> Unit
            }
        }
    }

    fun purchaseStreakFreeze(method: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true, feedbackMessage = null) }
            when (val res = repository.purchaseStreakFreeze(method)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        isBusy = false,
                        status = it.status.copy(
                            streakFreezes = res.data.streakFreezes,
                            gems = res.data.gems
                        ),
                        feedbackMessage = "Pelindung Streak berhasil diaktifkan!"
                    )
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isBusy = false, feedbackMessage = res.message)
                }
            }
        }
    }

    fun refillHearts(method: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true, feedbackMessage = null) }
            when (val res = repository.refillHearts(method)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        isBusy = false,
                        status = it.status.copy(
                            hearts = res.data.hearts,
                            gems = res.data.gems
                        ),
                        showZeroHeartsModal = res.data.hearts == 0,
                        feedbackMessage = "Nyawa berhasil dipulihkan!"
                    )
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isBusy = false, feedbackMessage = res.message)
                }
            }
        }
    }
}
