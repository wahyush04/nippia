package id.my.kulitech.nippia.ui.screens.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.LeagueOutcomeInfo
import id.my.kulitech.nippia.domain.repository.LeaderboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeagueOutcomeUiState(
    val isLoading: Boolean = true,
    val outcomeInfo: LeagueOutcomeInfo? = null,
    val isClaiming: Boolean = false,
    val claimedSuccess: Boolean = false,
    val snackbarMessage: String? = null
)

@HiltViewModel
class LeagueOutcomeViewModel @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeagueOutcomeUiState())
    val uiState: StateFlow<LeagueOutcomeUiState> = _uiState.asStateFlow()

    init {
        loadOutcome()
    }

    fun loadOutcome() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val res = leaderboardRepository.getLeagueOutcome()) {
                is AppResult.Success -> _uiState.update {
                    it.copy(isLoading = false, outcomeInfo = res.data)
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isLoading = false, snackbarMessage = res.message)
                }
            }
        }
    }

    fun claimReward(onClaimed: () -> Unit) {
        val outcome = _uiState.value.outcomeInfo?.outcome ?: "PROMOTED"
        viewModelScope.launch {
            _uiState.update { it.copy(isClaiming = true) }
            when (leaderboardRepository.claimLeagueOutcomeReward(if (outcome == "DEMOTED") "DEMOTED" else "PROMOTED")) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isClaiming = false, claimedSuccess = true) }
                    onClaimed()
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isClaiming = false) }
                    onClaimed()
                }
            }
        }
    }
}
