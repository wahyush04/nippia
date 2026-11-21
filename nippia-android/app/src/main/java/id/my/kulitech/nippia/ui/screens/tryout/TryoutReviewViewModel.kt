package id.my.kulitech.nippia.ui.screens.tryout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.TryoutAttemptReview
import id.my.kulitech.nippia.domain.usecase.GetAttemptReviewUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ReviewFilter {
    ALL,
    WRONG,
    CORRECT
}

data class TryoutReviewUiState(
    val isLoading: Boolean = true,
    val review: TryoutAttemptReview? = null,
    val filter: ReviewFilter = ReviewFilter.ALL
)

@HiltViewModel
class TryoutReviewViewModel @Inject constructor(
    private val getAttemptReviewUseCase: GetAttemptReviewUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TryoutReviewUiState())
    val uiState: StateFlow<TryoutReviewUiState> = _uiState.asStateFlow()

    fun loadReview(attemptId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getAttemptReviewUseCase(attemptId)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, review = result.data) }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onFilterSelected(filter: ReviewFilter) {
        _uiState.update { it.copy(filter = filter) }
    }
}
