package id.my.kulitech.nippia.ui.screens.mistakes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.MistakeAttemptResult
import id.my.kulitech.nippia.domain.model.MistakeBankItem
import id.my.kulitech.nippia.domain.model.MistakeBankOverview
import id.my.kulitech.nippia.domain.repository.LearningGamificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MistakeBankUiState(
    val isLoading: Boolean = true,
    val selectedCategory: String? = null,
    val unresolvedOnly: Boolean = true,
    val overview: MistakeBankOverview? = null,
    val activeRetryItem: MistakeBankItem? = null,
    val selectedOptionId: String? = null,
    val isSubmittingRetry: Boolean = false,
    val retryResult: MistakeAttemptResult? = null,
    val snackbarMessage: String? = null
)

@HiltViewModel
class MistakeBankViewModel @Inject constructor(
    private val repository: LearningGamificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MistakeBankUiState())
    val uiState: StateFlow<MistakeBankUiState> = _uiState.asStateFlow()

    init {
        loadMistakes()
    }

    fun loadMistakes() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val res = repository.getMistakeBank(state.selectedCategory, state.unresolvedOnly)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(isLoading = false, overview = res.data)
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isLoading = false, snackbarMessage = res.message)
                }
            }
        }
    }

    fun selectCategory(categoryCode: String?) {
        _uiState.update { it.copy(selectedCategory = categoryCode) }
        loadMistakes()
    }

    fun toggleUnresolvedOnly() {
        _uiState.update { it.copy(unresolvedOnly = !it.unresolvedOnly) }
        loadMistakes()
    }

    fun openRetry(item: MistakeBankItem) {
        _uiState.update {
            it.copy(
                activeRetryItem = item,
                selectedOptionId = null,
                retryResult = null
            )
        }
    }

    fun startQuickRetrySession() {
        val first = _uiState.value.overview?.items?.firstOrNull { !it.isResolved }
            ?: _uiState.value.overview?.items?.firstOrNull()
        if (first != null) {
            openRetry(first)
        } else {
            _uiState.update { it.copy(snackbarMessage = "Semua soal kesalahan sudah tuntas!") }
        }
    }

    fun selectRetryOption(optionId: String) {
        if (_uiState.value.retryResult != null) return
        _uiState.update { it.copy(selectedOptionId = optionId) }
    }

    fun submitRetry() {
        val item = _uiState.value.activeRetryItem ?: return
        val optId = _uiState.value.selectedOptionId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingRetry = true) }
            when (val res = repository.attemptMistake(item.questionId, optId)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSubmittingRetry = false,
                            retryResult = res.data
                        )
                    }
                    loadMistakes()
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isSubmittingRetry = false, snackbarMessage = res.message)
                }
            }
        }
    }

    fun closeRetry() {
        _uiState.update {
            it.copy(activeRetryItem = null, selectedOptionId = null, retryResult = null)
        }
    }

    fun clearResolved() {
        viewModelScope.launch {
            when (val res = repository.clearResolvedMistakes()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(snackbarMessage = "${res.data} riwayat soal tuntas dibersihkan.")
                    }
                    loadMistakes()
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(snackbarMessage = res.message)
                }
            }
        }
    }

    fun consumeSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
