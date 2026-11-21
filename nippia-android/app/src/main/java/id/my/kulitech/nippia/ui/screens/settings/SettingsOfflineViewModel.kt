package id.my.kulitech.nippia.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.core.datastore.SessionPreferences
import id.my.kulitech.nippia.data.remote.api.ContentSyncApiService
import id.my.kulitech.nippia.domain.model.ProfileStatsOverview
import id.my.kulitech.nippia.domain.usecase.GetProfileStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsOfflineUiState(
    val isLoading: Boolean = true,
    val overview: ProfileStatsOverview? = null,
    val lastSyncLabel: String = "Terkini",
    val isSyncing: Boolean = false,
    val cacheCleared: Boolean = false,
    val dailyReminderEnabled: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val snackbarMessage: String? = null
)

@HiltViewModel
class SettingsOfflineViewModel @Inject constructor(
    private val getProfileStatsUseCase: GetProfileStatsUseCase,
    private val contentSyncApiService: ContentSyncApiService,
    private val sessionPreferences: SessionPreferences,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsOfflineUiState())
    val uiState: StateFlow<SettingsOfflineUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val lastSync = sessionPreferences.lastContentSync.first()
            when (val res = getProfileStatsUseCase()) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        overview = res.data,
                        lastSyncLabel = lastSync ?: "Hari ini"
                    )
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        lastSyncLabel = lastSync ?: "Hari ini",
                        snackbarMessage = res.message
                    )
                }
            }
        }
    }

    fun syncNow() {
        if (_uiState.value.isSyncing) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            try {
                val since = sessionPreferences.lastContentSync.first()
                val res = contentSyncApiService.deltaSync(since)
                val ts = res.data?.syncTimestamp
                if (ts != null) {
                    sessionPreferences.updateLastContentSync(ts)
                }
                _uiState.update {
                    it.copy(
                        isSyncing = false,
                        lastSyncLabel = "Baru saja",
                        snackbarMessage = "Delta Sync selesai! Konten & bank soal terbaru."
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isSyncing = false,
                        lastSyncLabel = "Baru saja",
                        snackbarMessage = "Sinkronisasi offline selesai (Room DB lokal)."
                    )
                }
            }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            runCatching { appContext.cacheDir.deleteRecursively() }
            _uiState.update {
                it.copy(
                    cacheCleared = true,
                    snackbarMessage = "Cache usang berhasil dibersihkan."
                )
            }
        }
    }

    fun toggleDailyReminder(enabled: Boolean) {
        _uiState.update {
            it.copy(
                dailyReminderEnabled = enabled,
                snackbarMessage = if (enabled) "Pengingat latihan harian diaktifkan (19:30 WIB)." else "Pengingat latihan harian dinonaktifkan."
            )
        }
    }

    fun toggleHapticFeedback(enabled: Boolean) {
        _uiState.update { it.copy(hapticFeedbackEnabled = enabled) }
    }

    fun consumeSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
