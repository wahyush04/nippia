package id.my.kulitech.nippia.ui.screens.tryout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.TryoutHistoryOverview
import id.my.kulitech.nippia.domain.model.TryoutType
import id.my.kulitech.nippia.domain.repository.SupportProfileHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TryoutHistoryUiState(
    val isLoading: Boolean = true,
    val overview: TryoutHistoryOverview? = null,
    val selectedType: TryoutType? = null
)

@HiltViewModel
class TryoutHistoryViewModel @Inject constructor(
    private val repository: SupportProfileHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TryoutHistoryUiState())
    val uiState: StateFlow<TryoutHistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val res = repository.getTryoutHistory()) {
                is AppResult.Success -> _uiState.update { it.copy(isLoading = false, overview = res.data) }
                is AppResult.Error -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectFilter(type: TryoutType?) {
        _uiState.update { it.copy(selectedType = type) }
    }
}
