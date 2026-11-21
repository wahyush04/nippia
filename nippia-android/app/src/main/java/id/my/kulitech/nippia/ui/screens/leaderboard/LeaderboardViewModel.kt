package id.my.kulitech.nippia.ui.screens.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.LeaderboardEntry
import id.my.kulitech.nippia.domain.model.LeagueTier
import id.my.kulitech.nippia.domain.usecase.GetWeeklyLeaderboardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeaderboardUiState(
    val isLoading: Boolean = true,
    val leagueTier: LeagueTier = LeagueTier.BRONZE,
    val myRank: Int = 1,
    val myWeeklyXp: Int = 0,
    val entries: List<LeaderboardEntry> = emptyList(),
    val actionMessage: String? = null
) {
    val topThree: List<LeaderboardEntry> get() = entries.take(3)
    val myEntry: LeaderboardEntry? get() = entries.firstOrNull { it.isCurrentUser }
}

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val getWeeklyLeaderboardUseCase: GetWeeklyLeaderboardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboard()
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getWeeklyLeaderboardUseCase()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            leagueTier = result.data.leagueTier,
                            myRank = result.data.myRank,
                            myWeeklyXp = result.data.myWeeklyXp,
                            entries = result.data.entries
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onQuickPracticeClicked() {
        _uiState.update {
            it.copy(actionMessage = "Buka tab Belajar untuk mengerjakan unit mikro & kejar peringkat (+30 XP)!")
        }
    }

    fun onDismissMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }
}
