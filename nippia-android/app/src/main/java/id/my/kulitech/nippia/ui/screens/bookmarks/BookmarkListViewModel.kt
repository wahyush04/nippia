package id.my.kulitech.nippia.ui.screens.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.BookmarkOverview
import id.my.kulitech.nippia.domain.repository.SupportProfileHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookmarkListUiState(
    val isLoading: Boolean = true,
    val selectedTab: String = "QUESTION",
    val selectedCategory: String? = null,
    val overview: BookmarkOverview? = null,
    val snackbarMessage: String? = null
)

@HiltViewModel
class BookmarkListViewModel @Inject constructor(
    private val repository: SupportProfileHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookmarkListUiState())
    val uiState: StateFlow<BookmarkListUiState> = _uiState.asStateFlow()

    init {
        loadBookmarks()
    }

    fun loadBookmarks() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val res = repository.listBookmarks(state.selectedTab, state.selectedCategory)) {
                is AppResult.Success -> _uiState.update { it.copy(isLoading = false, overview = res.data) }
                is AppResult.Error -> _uiState.update { it.copy(isLoading = false, snackbarMessage = res.message) }
            }
        }
    }

    fun selectTab(itemType: String) {
        _uiState.update { it.copy(selectedTab = itemType) }
        loadBookmarks()
    }

    fun selectCategory(categoryCode: String?) {
        _uiState.update { it.copy(selectedCategory = categoryCode) }
        loadBookmarks()
    }

    fun removeBookmark(bookmarkId: String) {
        viewModelScope.launch {
            repository.deleteBookmark(bookmarkId)
            _uiState.update { it.copy(snackbarMessage = "Dihapus dari koleksi tersimpan.") }
            loadBookmarks()
        }
    }

    fun consumeSnackbar() = _uiState.update { it.copy(snackbarMessage = null) }
}
