package id.my.kulitech.nippia.ui.screens.guide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.UnitStudyGuide
import id.my.kulitech.nippia.domain.repository.LearningGamificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StudyGuideUiState(
    val isLoading: Boolean = true,
    val isBookmarked: Boolean = false,
    val guide: UnitStudyGuide? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class StudyGuideViewModel @Inject constructor(
    private val repository: LearningGamificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudyGuideUiState())
    val uiState: StateFlow<StudyGuideUiState> = _uiState.asStateFlow()

    fun loadGuide(unitId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val res = repository.getUnitStudyGuide(unitId)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(isLoading = false, guide = res.data)
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = res.message)
                }
            }
        }
    }

    fun toggleBookmark() {
        _uiState.update { it.copy(isBookmarked = !it.isBookmarked) }
    }
}
