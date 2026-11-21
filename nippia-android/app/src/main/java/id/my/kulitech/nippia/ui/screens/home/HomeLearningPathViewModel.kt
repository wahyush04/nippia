package id.my.kulitech.nippia.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.core.datastore.SessionPreferences
import id.my.kulitech.nippia.domain.model.GamificationSummary
import id.my.kulitech.nippia.domain.model.LearningCategoryPath
import id.my.kulitech.nippia.domain.model.SkdCategoryCode
import id.my.kulitech.nippia.domain.usecase.GetLearningPathUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeLearningPathUiState(
    val isLoading: Boolean = true,
    val selectedCategory: SkdCategoryCode = SkdCategoryCode.TWK,
    val categories: List<LearningCategoryPath> = emptyList(),
    val gamification: GamificationSummary = GamificationSummary(
        totalXp = 0,
        weeklyXp = 0,
        gems = 0,
        currentStreak = 0,
        longestStreak = 0,
        streakFreezes = 0,
        hearts = 5,
        leagueTier = "BRONZE"
    ),
    val pendingMistakesCount: Int = 0,
    val isPro: Boolean = false,
    val showCategorySheet: Boolean = false,
    val showGuideSheet: Boolean = false,
    val selectedUnitMessage: String? = null
) {
    val activeCategoryPath: LearningCategoryPath?
        get() = categories.firstOrNull { it.code == selectedCategory } ?: categories.firstOrNull()
}

@HiltViewModel
class HomeLearningPathViewModel @Inject constructor(
    private val getLearningPathUseCase: GetLearningPathUseCase,
    private val sessionPreferences: SessionPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeLearningPathUiState())
    val uiState: StateFlow<HomeLearningPathUiState> = _uiState.asStateFlow()

    init {
        loadHome()
    }

    fun loadHome(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.update { it.copy(isLoading = true) }
            }
            val preferred = SkdCategoryCode.fromCode(sessionPreferences.selectedCategory.first())
            val isPro = sessionPreferences.isProUser.first()

            when (val result = getLearningPathUseCase()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            selectedCategory = preferred,
                            categories = result.data.categories,
                            gamification = result.data.gamification,
                            pendingMistakesCount = result.data.pendingMistakesCount,
                            isPro = isPro
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun refreshSilently() = loadHome(showLoading = false)

    fun onOpenCategorySheet() {
        _uiState.update { it.copy(showCategorySheet = true) }
    }

    fun onDismissCategorySheet() {
        _uiState.update { it.copy(showCategorySheet = false) }
    }

    fun onSelectCategory(category: SkdCategoryCode) {
        viewModelScope.launch {
            sessionPreferences.setSelectedCategory(category.code)
            _uiState.update {
                it.copy(
                    selectedCategory = category,
                    showCategorySheet = false
                )
            }
        }
    }

    fun onOpenGuideSheet() {
        _uiState.update { it.copy(showGuideSheet = true) }
    }

    fun onDismissGuideSheet() {
        _uiState.update { it.copy(showGuideSheet = false) }
    }

    fun onUnitClicked(title: String, isLocked: Boolean) {
        val msg = if (isLocked) {
            "Selesaikan unit sebelumnya untuk membuka $title."
        } else {
            "Unit \"$title\" siap dikerjakan! Layar Kuis Mikro akan diimplementasikan berikutnya."
        }
        _uiState.update { it.copy(selectedUnitMessage = msg) }
    }

    fun onDismissUnitMessage() {
        _uiState.update { it.copy(selectedUnitMessage = null) }
    }
}
