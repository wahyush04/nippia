package id.my.kulitech.nippia.ui.screens.path

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.core.datastore.SessionPreferences
import id.my.kulitech.nippia.domain.model.SkdCategoryCode
import id.my.kulitech.nippia.domain.usecase.GetLearningPathUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChooseLearningPathUiState(
    val selectedCategory: SkdCategoryCode = SkdCategoryCode.TWK,
    val gems: Int = 10,
    val hearts: Int = 5,
    val isSaving: Boolean = false
)

sealed interface ChoosePathNavEvent {
    data object ToHome : ChoosePathNavEvent
}

@HiltViewModel
class ChooseLearningPathViewModel @Inject constructor(
    private val getLearningPathUseCase: GetLearningPathUseCase,
    private val sessionPreferences: SessionPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChooseLearningPathUiState())
    val uiState: StateFlow<ChooseLearningPathUiState> = _uiState.asStateFlow()

    private val _navEvents = MutableSharedFlow<ChoosePathNavEvent>()
    val navEvents: SharedFlow<ChoosePathNavEvent> = _navEvents.asSharedFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            when (val result = getLearningPathUseCase()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            gems = result.data.gamification.gems,
                            hearts = result.data.gamification.hearts
                        )
                    }
                }
                is AppResult.Error -> Unit
            }
        }
    }

    fun onCategorySelected(category: SkdCategoryCode) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onConfirmPathClick() {
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            sessionPreferences.setSelectedCategory(_uiState.value.selectedCategory.code)
            _uiState.update { it.copy(isSaving = false) }
            _navEvents.emit(ChoosePathNavEvent.ToHome)
        }
    }
}
