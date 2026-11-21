package id.my.kulitech.nippia.ui.screens.section

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.core.datastore.SessionPreferences
import id.my.kulitech.nippia.domain.model.LearningCategoryPath
import id.my.kulitech.nippia.domain.model.LearningSection
import id.my.kulitech.nippia.domain.model.SkdCategoryCode
import id.my.kulitech.nippia.domain.usecase.GetLearningPathUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SectionListUiState(
    val isLoading: Boolean = true,
    val categoryCode: SkdCategoryCode = SkdCategoryCode.TWK,
    val categoryPath: LearningCategoryPath? = null,
    val sections: List<LearningSection> = emptyList(),
    val activeGuideSection: LearningSection? = null
) {
    val completedUnits: Int get() = categoryPath?.completedCount ?: 0
    val totalUnits: Int get() = categoryPath?.totalCount ?: 0
    val remainingUnits: Int get() = (totalUnits - completedUnits).coerceAtLeast(0)
    val progressFraction: Float get() = categoryPath?.progressFraction ?: 0f
    val passingGrade: Int get() = categoryPath?.passingGrade ?: when (categoryCode) {
        SkdCategoryCode.TWK -> 65
        SkdCategoryCode.TIU -> 80
        SkdCategoryCode.TKP -> 166
    }
}

@HiltViewModel
class SectionListViewModel @Inject constructor(
    private val getLearningPathUseCase: GetLearningPathUseCase,
    private val sessionPreferences: SessionPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SectionListUiState())
    val uiState: StateFlow<SectionListUiState> = _uiState.asStateFlow()

    fun loadCategory(categoryCode: String) {
        val code = SkdCategoryCode.fromCode(categoryCode)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, categoryCode = code) }
            sessionPreferences.setSelectedCategory(code.code)

            when (val result = getLearningPathUseCase(code.code)) {
                is AppResult.Success -> {
                    val cat = result.data.categories.firstOrNull { it.code == code }
                        ?: result.data.categories.firstOrNull()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            categoryCode = code,
                            categoryPath = cat,
                            sections = cat?.sections.orEmpty()
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onOpenGuide(section: LearningSection) {
        _uiState.update { it.copy(activeGuideSection = section) }
    }

    fun onDismissGuide() {
        _uiState.update { it.copy(activeGuideSection = null) }
    }
}
