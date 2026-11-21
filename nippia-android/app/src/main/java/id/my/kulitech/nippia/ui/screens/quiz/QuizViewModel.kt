package id.my.kulitech.nippia.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.QuizAnswerSubmission
import id.my.kulitech.nippia.domain.model.QuizOption
import id.my.kulitech.nippia.domain.model.QuizQuestion
import id.my.kulitech.nippia.domain.model.QuizQuestionType
import id.my.kulitech.nippia.domain.model.UnitCompletionResult
import id.my.kulitech.nippia.domain.repository.LearningGamificationRepository
import id.my.kulitech.nippia.domain.usecase.CompleteUnitUseCase
import id.my.kulitech.nippia.domain.usecase.GetUnitQuestionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val isLoading: Boolean = true,
    val unitId: String = "",
    val unitTitle: String = "",
    val xpReward: Int = 15,
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionId: String? = null,
    val isChecked: Boolean = false,
    val showHint: Boolean = false,
    val hearts: Int = 5,
    val heartsLost: Int = 0,
    val comboLevel: Int = 0,
    val correctCount: Int = 0,
    val mascotCorrectTrigger: Int = 0,
    val mascotWrongTrigger: Int = 0,
    val heartCrackTrigger: Int = 0,
    val zoomImageUrl: String? = null,
    val isSubmitting: Boolean = false,
    val completionResult: UnitCompletionResult? = null,
    val gems: Int = 480,
    val currentStreak: Int = 7,
    val isRefillingHearts: Boolean = false,
    val heartRefillMessage: String? = null,
    val durationSeconds: Int = 165
) {
    val currentQuestion: QuizQuestion? get() = questions.getOrNull(currentIndex)
    val totalQuestions: Int get() = questions.size
    val progressFraction: Float
        get() = if (totalQuestions == 0) 0f else (currentIndex + if (isChecked) 1 else 0).toFloat() / totalQuestions
    val selectedOption: QuizOption?
        get() = currentQuestion?.options?.firstOrNull { it.id == selectedOptionId }
    val isLastQuestion: Boolean
        get() = questions.isNotEmpty() && currentIndex >= questions.lastIndex

    val accuracyTier: Int
        get() {
            if (totalQuestions == 0) return 3
            val ratio = correctCount.toFloat() / totalQuestions
            return when {
                ratio >= 0.85f -> 3
                ratio >= 0.55f -> 2
                else -> 1
            }
        }
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getUnitQuestionsUseCase: GetUnitQuestionsUseCase,
    private val completeUnitUseCase: CompleteUnitUseCase,
    private val gamificationRepository: LearningGamificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val recordedAnswers = mutableListOf<QuizAnswerSubmission>()
    private var startedAtMs: Long = System.currentTimeMillis()

    fun loadUnit(unitId: String, categoryCode: String) {
        viewModelScope.launch {
            startedAtMs = System.currentTimeMillis()
            recordedAnswers.clear()
            _uiState.update { it.copy(isLoading = true, unitId = unitId) }

            when (val result = getUnitQuestionsUseCase(unitId = unitId, fallbackCategory = categoryCode)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            unitTitle = result.data.title,
                            xpReward = result.data.xpReward,
                            questions = result.data.questions
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onOptionSelected(optionId: String) {
        if (_uiState.value.isChecked) return
        _uiState.update { it.copy(selectedOptionId = optionId) }
    }

    fun onToggleHint() {
        _uiState.update { it.copy(showHint = !it.showHint) }
    }

    fun onOpenZoomImage(url: String) {
        _uiState.update { it.copy(zoomImageUrl = url) }
    }

    fun onDismissZoomImage() {
        _uiState.update { it.copy(zoomImageUrl = null) }
    }

    fun onCheckAnswer() {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        val option = state.selectedOption ?: return
        if (state.isChecked) return

        recordedAnswers.removeAll { it.questionId == question.id }
        recordedAnswers.add(QuizAnswerSubmission(question.id, option.id))

        val isPositive = when (question.type) {
            QuizQuestionType.MULTIPLE_CHOICE -> option.score > 0
            QuizQuestionType.WEIGHTED_SCALE -> option.score >= 4
        }
        val shouldDeductHeart = question.type == QuizQuestionType.MULTIPLE_CHOICE && !isPositive

        _uiState.update { current ->
            val nextHearts = if (shouldDeductHeart) (current.hearts - 1).coerceAtLeast(0) else current.hearts
            val nextHeartsLost = if (shouldDeductHeart) current.heartsLost + 1 else current.heartsLost
            current.copy(
                isChecked = true,
                hearts = nextHearts,
                heartsLost = nextHeartsLost,
                comboLevel = if (isPositive) (current.comboLevel + 1).coerceAtMost(5) else 0,
                correctCount = if (isPositive) current.correctCount + 1 else current.correctCount,
                mascotCorrectTrigger = if (isPositive) current.mascotCorrectTrigger + 1 else current.mascotCorrectTrigger,
                mascotWrongTrigger = if (!isPositive) current.mascotWrongTrigger + 1 else current.mascotWrongTrigger,
                heartCrackTrigger = if (shouldDeductHeart) current.heartCrackTrigger + 1 else current.heartCrackTrigger
            )
        }
    }

    fun onContinueAfterFeedback() {
        val state = _uiState.value
        if (!state.isChecked) return

        if (!state.isLastQuestion) {
            _uiState.update {
                it.copy(
                    currentIndex = it.currentIndex + 1,
                    selectedOptionId = null,
                    isChecked = false,
                    showHint = false
                )
            }
        } else {
            submitUnit()
        }
    }

    private fun submitUnit() {
        val state = _uiState.value
        if (state.isSubmitting) return
        val durationSeconds = ((System.currentTimeMillis() - startedAtMs) / 1000L).toInt().coerceAtLeast(1)

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            when (
                val result = completeUnitUseCase(
                    unitId = state.unitId,
                    answers = recordedAnswers.toList(),
                    heartsLost = state.heartsLost,
                    durationSeconds = durationSeconds
                )
            ) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            durationSeconds = durationSeconds,
                            completionResult = result.data
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            durationSeconds = durationSeconds,
                            completionResult = UnitCompletionResult(
                                xpEarned = state.xpReward,
                                totalXp = state.xpReward,
                                currentStreak = 1,
                                hearts = state.hearts,
                                unlockedNextUnitId = null
                            )
                        )
                    }
                }
            }
        }
    }

    fun refillHearts(method: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefillingHearts = true, heartRefillMessage = null) }
            when (val res = gamificationRepository.refillHearts(method)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        isRefillingHearts = false,
                        hearts = res.data.hearts,
                        gems = res.data.gems
                    )
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(
                        isRefillingHearts = false,
                        heartRefillMessage = res.message
                    )
                }
            }
        }
    }
}
