package id.my.kulitech.nippia.ui.screens.tryout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.QuizAnswerSubmission
import id.my.kulitech.nippia.domain.model.TryoutQuestion
import id.my.kulitech.nippia.domain.model.TryoutSession
import id.my.kulitech.nippia.domain.model.TryoutSubmitResult
import id.my.kulitech.nippia.domain.usecase.StartTryoutUseCase
import id.my.kulitech.nippia.domain.usecase.SubmitTryoutUseCase
import kotlinx.coroutines.Job
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

enum class AnswerSheetFilter {
    ALL,
    TWK,
    TIU,
    TKP,
    FLAGGED
}

data class TryoutSessionUiState(
    val packageId: String = "",
    val title: String = "Tryout Akbar Nasional SKD #12",
    val isStarting: Boolean = false,
    val session: TryoutSession? = null,
    val currentIndex: Int = 0,
    val selectedOptions: Map<String, String> = emptyMap(),
    val flaggedQuestionIds: Set<String> = emptySet(),
    val remainingSeconds: Int = 100 * 60,
    val elapsedSeconds: Int = 0,
    val isLargeFont: Boolean = false,
    val answerSheetFilter: AnswerSheetFilter = AnswerSheetFilter.ALL,
    val showSubmitConfirmDialog: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitResult: TryoutSubmitResult? = null,
    val zoomImageUrl: String? = null,
    val errorMessage: String? = null
) {
    val questions: List<TryoutQuestion> get() = session?.questions.orEmpty()
    val totalQuestions: Int get() = questions.size
    val currentQuestion: TryoutQuestion? get() = questions.getOrNull(currentIndex)
    val answeredCount: Int get() = selectedOptions.size
    val flaggedCount: Int get() = flaggedQuestionIds.size
    val emptyCount: Int get() = (totalQuestions - answeredCount).coerceAtLeast(0)
    val formattedRemainingTime: String
        get() {
            val mins = remainingSeconds / 60
            val secs = remainingSeconds % 60
            return "%02d:%02d".format(mins, secs)
        }
    val formattedElapsedTime: String
        get() {
            val mins = elapsedSeconds / 60
            val secs = elapsedSeconds % 60
            return "${mins}m ${secs}s"
        }
}

sealed interface TryoutNavEvent {
    data object ToExam : TryoutNavEvent
    data object ToReport : TryoutNavEvent
}

@HiltViewModel
class TryoutSessionViewModel @Inject constructor(
    private val startTryoutUseCase: StartTryoutUseCase,
    private val submitTryoutUseCase: SubmitTryoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TryoutSessionUiState())
    val uiState: StateFlow<TryoutSessionUiState> = _uiState.asStateFlow()

    private val _navEvents = MutableSharedFlow<TryoutNavEvent>()
    val navEvents: SharedFlow<TryoutNavEvent> = _navEvents.asSharedFlow()

    private var timerJob: Job? = null

    fun initLobby(packageId: String, title: String) {
        if (_uiState.value.packageId == packageId && _uiState.value.session != null) return
        _uiState.update {
            it.copy(
                packageId = packageId,
                title = title,
                session = null,
                submitResult = null,
                selectedOptions = emptyMap(),
                flaggedQuestionIds = emptySet(),
                currentIndex = 0
            )
        }
    }

    fun startExam() {
        val state = _uiState.value
        if (state.isStarting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isStarting = true, errorMessage = null) }
            when (val result = startTryoutUseCase(state.packageId, state.title)) {
                is AppResult.Success -> {
                    val session = result.data
                    _uiState.update {
                        it.copy(
                            isStarting = false,
                            session = session,
                            title = session.title,
                            remainingSeconds = session.durationMinutes * 60,
                            elapsedSeconds = 0,
                            currentIndex = 0
                        )
                    }
                    startCountdownTimer()
                    _navEvents.emit(TryoutNavEvent.ToExam)
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(isStarting = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    private fun startCountdownTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0 && _uiState.value.submitResult == null) {
                delay(1000)
                _uiState.update {
                    it.copy(
                        remainingSeconds = (it.remainingSeconds - 1).coerceAtLeast(0),
                        elapsedSeconds = it.elapsedSeconds + 1
                    )
                }
            }
        }
    }

    fun onSelectOption(questionId: String, optionId: String) {
        _uiState.update { state ->
            state.copy(selectedOptions = state.selectedOptions + (questionId to optionId))
        }
    }

    fun onToggleFlag(questionId: String) {
        _uiState.update { state ->
            val updated = if (questionId in state.flaggedQuestionIds) {
                state.flaggedQuestionIds - questionId
            } else {
                state.flaggedQuestionIds + questionId
            }
            state.copy(flaggedQuestionIds = updated)
        }
    }

    fun onToggleFontSize() {
        _uiState.update { it.copy(isLargeFont = !it.isLargeFont) }
    }

    fun onPreviousQuestion() {
        _uiState.update {
            it.copy(currentIndex = (it.currentIndex - 1).coerceAtLeast(0))
        }
    }

    fun onNextQuestion() {
        _uiState.update {
            val maxIdx = (it.totalQuestions - 1).coerceAtLeast(0)
            it.copy(currentIndex = (it.currentIndex + 1).coerceAtMost(maxIdx))
        }
    }

    fun onJumpToQuestion(index: Int) {
        val maxIdx = (_uiState.value.totalQuestions - 1).coerceAtLeast(0)
        _uiState.update { it.copy(currentIndex = index.coerceIn(0, maxIdx)) }
    }

    fun onSelectAnswerSheetFilter(filter: AnswerSheetFilter) {
        _uiState.update { it.copy(answerSheetFilter = filter) }
    }

    fun onOpenSubmitConfirm() {
        _uiState.update { it.copy(showSubmitConfirmDialog = true) }
    }

    fun onDismissSubmitConfirm() {
        _uiState.update { it.copy(showSubmitConfirmDialog = false) }
    }

    fun onOpenZoomImage(url: String) {
        _uiState.update { it.copy(zoomImageUrl = url) }
    }

    fun onDismissZoomImage() {
        _uiState.update { it.copy(zoomImageUrl = null) }
    }

    fun submitExam() {
        val state = _uiState.value
        val attemptId = state.session?.attemptId ?: return
        if (state.isSubmitting) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSubmitting = true, showSubmitConfirmDialog = false, errorMessage = null)
            }
            val answers = state.selectedOptions.entries.map { (qId, optId) ->
                QuizAnswerSubmission(questionId = qId, selectedOptionId = optId)
            }
            when (val result = submitTryoutUseCase(attemptId = attemptId, answers = answers)) {
                is AppResult.Success -> {
                    timerJob?.cancel()
                    _uiState.update {
                        it.copy(isSubmitting = false, submitResult = result.data)
                    }
                    _navEvents.emit(TryoutNavEvent.ToReport)
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(isSubmitting = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
