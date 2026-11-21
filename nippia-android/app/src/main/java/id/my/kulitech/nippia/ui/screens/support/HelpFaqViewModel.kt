package id.my.kulitech.nippia.ui.screens.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.FaqCatalog
import id.my.kulitech.nippia.domain.repository.SupportProfileHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HelpFaqUiState(
    val isLoading: Boolean = true,
    val catalog: FaqCatalog? = null,
    val searchQuery: String = "",
    val expandedFaqId: String? = null,
    val showReportModal: Boolean = false,
    val reportCategory: String = "Kunci Jawaban",
    val reportDescription: String = "",
    val isSubmittingReport: Boolean = false,
    val snackbarMessage: String? = null
)

@HiltViewModel
class HelpFaqViewModel @Inject constructor(
    private val repository: SupportProfileHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HelpFaqUiState())
    val uiState: StateFlow<HelpFaqUiState> = _uiState.asStateFlow()

    init {
        loadFaq()
    }

    private fun loadFaq() {
        viewModelScope.launch {
            when (val res = repository.getFaqCatalog()) {
                is AppResult.Success -> _uiState.update { it.copy(isLoading = false, catalog = res.data) }
                is AppResult.Error -> _uiState.update { it.copy(isLoading = false, snackbarMessage = res.message) }
            }
        }
    }

    fun onSearchChange(q: String) = _uiState.update { it.copy(searchQuery = q) }
    fun toggleFaq(id: String) = _uiState.update { it.copy(expandedFaqId = if (it.expandedFaqId == id) null else id) }
    fun openReportModal() = _uiState.update { it.copy(showReportModal = true) }
    fun closeReportModal() = _uiState.update { it.copy(showReportModal = false, reportDescription = "") }
    fun onReportCategoryChange(c: String) = _uiState.update { it.copy(reportCategory = c) }
    fun onReportDescriptionChange(d: String) = _uiState.update { it.copy(reportDescription = d) }

    fun submitReport() {
        val state = _uiState.value
        if (state.reportDescription.trim().length < 5) {
            _uiState.update { it.copy(snackbarMessage = "Mohon jelaskan kendala minimal 5 karakter.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingReport = true) }
            when (val res = repository.submitQuestionReport(null, state.reportCategory, state.reportDescription.trim())) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        isSubmittingReport = false,
                        showReportModal = false,
                        reportDescription = "",
                        snackbarMessage = res.data
                    )
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isSubmittingReport = false, snackbarMessage = res.message)
                }
            }
        }
    }

    fun consumeSnackbar() = _uiState.update { it.copy(snackbarMessage = null) }
}
