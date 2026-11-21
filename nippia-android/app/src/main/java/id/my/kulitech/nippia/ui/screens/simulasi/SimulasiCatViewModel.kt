package id.my.kulitech.nippia.ui.screens.simulasi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.ScheduledTryoutEvent
import id.my.kulitech.nippia.domain.model.TryoutPackageItem
import id.my.kulitech.nippia.domain.usecase.GetTryoutCatalogUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SimulasiModeTab {
    LIVE_AKBAR,
    MANDIRI
}

data class SimulasiCatUiState(
    val isLoading: Boolean = true,
    val selectedMode: SimulasiModeTab = SimulasiModeTab.LIVE_AKBAR,
    val packages: List<TryoutPackageItem> = emptyList(),
    val scheduledEvent: ScheduledTryoutEvent? = null,
    val showPassingGradeModal: Boolean = false,
    val actionMessage: String? = null
)

@HiltViewModel
class SimulasiCatViewModel @Inject constructor(
    private val getTryoutCatalogUseCase: GetTryoutCatalogUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SimulasiCatUiState())
    val uiState: StateFlow<SimulasiCatUiState> = _uiState.asStateFlow()

    init {
        loadCatalog()
    }

    fun loadCatalog() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getTryoutCatalogUseCase()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            packages = result.data.packages,
                            scheduledEvent = result.data.scheduledEvent
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onModeSelected(mode: SimulasiModeTab) {
        _uiState.update { it.copy(selectedMode = mode) }
    }

    fun onOpenPassingGradeModal() {
        _uiState.update { it.copy(showPassingGradeModal = true) }
    }

    fun onDismissPassingGradeModal() {
        _uiState.update { it.copy(showPassingGradeModal = false) }
    }

    fun onStartActionClicked(label: String) {
        _uiState.update {
            it.copy(actionMessage = "$label — ruang ujian CAT siap diimplementasikan pada tahap berikutnya.")
        }
    }

    fun onDismissMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }
}
