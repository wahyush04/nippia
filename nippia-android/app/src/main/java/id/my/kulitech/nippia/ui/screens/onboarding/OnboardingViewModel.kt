package id.my.kulitech.nippia.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.CpnsFormation
import id.my.kulitech.nippia.domain.model.DailyGoalOption
import id.my.kulitech.nippia.domain.model.DiagnosticOption
import id.my.kulitech.nippia.domain.model.DiagnosticQuestion
import id.my.kulitech.nippia.domain.model.DiagnosticScore
import id.my.kulitech.nippia.domain.model.FormationCategory
import id.my.kulitech.nippia.domain.model.OnboardingSubmissionResult
import id.my.kulitech.nippia.domain.usecase.GetDiagnosticQuestionsUseCase
import id.my.kulitech.nippia.domain.usecase.GetFormationsUseCase
import id.my.kulitech.nippia.domain.usecase.SubmitOnboardingUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val isLoadingFormations: Boolean = true,
    val formations: List<CpnsFormation> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: FormationCategory = FormationCategory.ALL,
    val selectedFormation: CpnsFormation? = null,

    val selectedDailyGoal: DailyGoalOption = DailyGoalOption.REGULER,
    val dailyReminderEnabled: Boolean = true,
    val dailyReminderTime: String = "19:30",

    val diagnosticQuestions: List<DiagnosticQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionLabel: String? = null,
    val isAnswerChecked: Boolean = false,
    val twkCorrect: Boolean = false,
    val tiuCorrect: Boolean = false,
    val tkpScore: Int = 0,
    val mascotCorrectTrigger: Int = 0,
    val mascotWrongTrigger: Int = 0,

    val isSubmitting: Boolean = false,
    val completionResult: OnboardingSubmissionResult? = null,
    val errorMessage: String? = null
) {
    val filteredFormations: List<CpnsFormation>
        get() = formations.filter { item ->
            val categoryMatch = selectedCategory.apiValue == null || item.category == selectedCategory.apiValue
            val q = searchQuery.trim().lowercase()
            val queryMatch = q.isEmpty() ||
                item.institution.lowercase().contains(q) ||
                item.position.lowercase().contains(q) ||
                item.qualification.lowercase().contains(q)
            categoryMatch && queryMatch
        }

    val currentQuestion: DiagnosticQuestion?
        get() = diagnosticQuestions.getOrNull(currentQuestionIndex)

    val isLastDiagnosticQuestion: Boolean
        get() = diagnosticQuestions.isNotEmpty() && currentQuestionIndex >= diagnosticQuestions.lastIndex
}

sealed interface OnboardingNavEvent {
    data object ToHome : OnboardingNavEvent
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val getFormationsUseCase: GetFormationsUseCase,
    private val getDiagnosticQuestionsUseCase: GetDiagnosticQuestionsUseCase,
    private val submitOnboardingUseCase: SubmitOnboardingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _navEvents = MutableSharedFlow<OnboardingNavEvent>()
    val navEvents: SharedFlow<OnboardingNavEvent> = _navEvents.asSharedFlow()

    init {
        loadOnboardingData()
    }

    private fun loadOnboardingData() {
        viewModelScope.launch {
            val formationsResult = getFormationsUseCase()
            val questionsResult = getDiagnosticQuestionsUseCase()

            val formations = (formationsResult as? AppResult.Success)?.data.orEmpty()
            val questions = (questionsResult as? AppResult.Success)?.data.orEmpty()

            _uiState.update {
                it.copy(
                    isLoadingFormations = false,
                    formations = formations,
                    selectedFormation = it.selectedFormation ?: formations.firstOrNull(),
                    diagnosticQuestions = questions
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelected(category: FormationCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onFormationSelected(formation: CpnsFormation) {
        _uiState.update { it.copy(selectedFormation = formation) }
    }

    fun onDailyGoalSelected(goal: DailyGoalOption) {
        _uiState.update { it.copy(selectedDailyGoal = goal) }
    }

    fun onReminderToggled(enabled: Boolean) {
        _uiState.update { it.copy(dailyReminderEnabled = enabled) }
    }

    fun onReminderTimeChanged(hour: Int, minute: Int) {
        val formatted = "%02d:%02d".format(hour, minute)
        _uiState.update { it.copy(dailyReminderTime = formatted) }
    }

    fun useDefaultSchedule() {
        _uiState.update {
            it.copy(
                selectedDailyGoal = DailyGoalOption.REGULER,
                dailyReminderEnabled = true,
                dailyReminderTime = "19:30"
            )
        }
    }

    fun onOptionSelected(label: String) {
        if (_uiState.value.isAnswerChecked) return
        _uiState.update { it.copy(selectedOptionLabel = label) }
    }

    fun onCheckAnswerOrAdvance() {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        val selectedLabel = state.selectedOptionLabel ?: return

        if (!state.isAnswerChecked) {
            val chosenOption = question.options.firstOrNull { it.label == selectedLabel } ?: return
            val isPositive = isPositiveAnswer(question, chosenOption)

            _uiState.update { current ->
                current.copy(
                    isAnswerChecked = true,
                    twkCorrect = if (question.categoryCode == "TWK") chosenOption.score > 0 else current.twkCorrect,
                    tiuCorrect = if (question.categoryCode == "TIU") chosenOption.score > 0 else current.tiuCorrect,
                    tkpScore = if (question.categoryCode == "TKP") chosenOption.score else current.tkpScore,
                    mascotCorrectTrigger = if (isPositive) current.mascotCorrectTrigger + 1 else current.mascotCorrectTrigger,
                    mascotWrongTrigger = if (!isPositive) current.mascotWrongTrigger + 1 else current.mascotWrongTrigger
                )
            }
            return
        }

        if (!state.isLastDiagnosticQuestion) {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    selectedOptionLabel = null,
                    isAnswerChecked = false
                )
            }
        } else {
            submitOnboarding(diagnosticCompleted = true)
        }
    }

    fun skipDiagnosticAndSubmit() {
        submitOnboarding(diagnosticCompleted = false)
    }

    private fun submitOnboarding(diagnosticCompleted: Boolean) {
        val state = _uiState.value
        if (state.isSubmitting) return

        val formation = state.selectedFormation
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            val score = if (diagnosticCompleted) {
                DiagnosticScore(
                    twkCorrect = state.twkCorrect,
                    tiuCorrect = state.tiuCorrect,
                    tkpScore = state.tkpScore
                )
            } else {
                null
            }

            when (
                val result = submitOnboardingUseCase(
                    targetInstitution = formation?.institution ?: "Kementerian Hukum dan HAM",
                    targetPosition = formation?.position ?: "Penjaga Tahanan",
                    dailyGoal = state.selectedDailyGoal,
                    dailyReminderEnabled = state.dailyReminderEnabled,
                    dailyReminderTime = state.dailyReminderTime,
                    diagnosticCompleted = diagnosticCompleted,
                    diagnosticScore = score
                )
            ) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            completionResult = result.data
                        )
                    }
                    if (!diagnosticCompleted || result.data.bonusXp == 0) {
                        _navEvents.emit(OnboardingNavEvent.ToHome)
                    }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun onCompletionCelebrationDismissed() {
        viewModelScope.launch {
            _uiState.update { it.copy(completionResult = null) }
            _navEvents.emit(OnboardingNavEvent.ToHome)
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun isPositiveAnswer(question: DiagnosticQuestion, option: DiagnosticOption): Boolean {
        return if (question.questionType == "WEIGHTED_SCALE") {
            option.score >= 4
        } else {
            option.score > 0
        }
    }
}
