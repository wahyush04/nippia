package id.my.kulitech.nippia.ui.screens.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.QuizAnswerSubmission
import id.my.kulitech.nippia.domain.model.RescueQuizQuestion
import id.my.kulitech.nippia.domain.model.RescueQuizSession
import id.my.kulitech.nippia.domain.model.RescueQuizSubmitResult
import id.my.kulitech.nippia.domain.repository.LeaderboardRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RescueQuizUiState(
    val isLoading: Boolean = true,
    val session: RescueQuizSession? = null,
    val currentIndex: Int = 0,
    val selectedOptionId: String? = null,
    val isChecked: Boolean = false,
    val remainingSeconds: Int = 180,
    val isSubmitting: Boolean = false,
    val result: RescueQuizSubmitResult? = null,
    val errorMessage: String? = null
) {
    val currentQuestion: RescueQuizQuestion? get() = session?.questions?.getOrNull(currentIndex)
    val totalQuestions: Int get() = session?.questions?.size ?: 0
    val formattedTime: String
        get() = "%02d:%02d".format(remainingSeconds / 60, remainingSeconds % 60)
}

@HiltViewModel
class RescueQuizViewModel @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RescueQuizUiState())
    val uiState: StateFlow<RescueQuizUiState> = _uiState.asStateFlow()

    private val recordedAnswers = mutableListOf<QuizAnswerSubmission>()
    private var timerJob: Job? = null

    init {
        loadRescueQuiz()
    }

    fun loadRescueQuiz() {
        viewModelScope.launch {
            recordedAnswers.clear()
            _uiState.update { it.copy(isLoading = true, result = null, currentIndex = 0) }
            when (val res = leaderboardRepository.getRescueQuiz()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            session = res.data,
                            remainingSeconds = res.data.timeLimitSeconds
                        )
                    }
                    startTimer()
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = res.message)
                }
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0 && _uiState.value.result == null) {
                delay(1000L)
                _uiState.update { it.copy(remainingSeconds = (it.remainingSeconds - 1).coerceAtLeast(0)) }
            }
        }
    }

    fun selectOption(optionId: String) {
        if (_uiState.value.isChecked) return
        _uiState.update { it.copy(selectedOptionId = optionId) }
    }

    fun checkOrAdvance() {
        val state = _uiState.value
        val q = state.currentQuestion ?: return
        val optId = state.selectedOptionId ?: return

        if (!state.isChecked) {
            recordedAnswers.add(QuizAnswerSubmission(q.id, optId))
            _uiState.update { it.copy(isChecked = true) }
            return
        }

        if (state.currentIndex < state.totalQuestions - 1) {
            _uiState.update {
                it.copy(
                    currentIndex = it.currentIndex + 1,
                    selectedOptionId = null,
                    isChecked = false
                )
            }
        } else {
            submitRescue()
        }
    }

    private fun submitRescue() {
        timerJob?.cancel()
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            when (val res = leaderboardRepository.submitRescueQuiz(recordedAnswers.toList())) {
                is AppResult.Success -> _uiState.update {
                    it.copy(isSubmitting = false, result = res.data)
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isSubmitting = false, errorMessage = res.message)
                }
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
