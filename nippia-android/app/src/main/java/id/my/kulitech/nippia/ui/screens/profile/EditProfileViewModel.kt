package id.my.kulitech.nippia.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.UpdateProfileInput
import id.my.kulitech.nippia.domain.repository.SupportProfileHistoryRepository
import id.my.kulitech.nippia.domain.usecase.GetProfileStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUiState(
    val isLoading: Boolean = true,
    val displayName: String = "",
    val targetInstitution: String = "",
    val targetPosition: String = "",
    val dailyGoal: String = "REGULER",
    val dailyReminderEnabled: Boolean = true,
    val isSaving: Boolean = false,
    val showDeleteModal: Boolean = false,
    val deleteConfirmationText: String = "",
    val isDeleting: Boolean = false,
    val snackbarMessage: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getProfileStatsUseCase: GetProfileStatsUseCase,
    private val repository: SupportProfileHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadCurrentProfile()
    }

    private fun loadCurrentProfile() {
        viewModelScope.launch {
            when (val res = getProfileStatsUseCase()) {
                is AppResult.Success -> {
                    val user = res.data.user
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            displayName = user.displayName ?: "",
                            targetInstitution = user.targetInstitution ?: "Kemenkeu RI",
                            targetPosition = user.targetPosition ?: "Analis Kebijakan",
                            dailyGoal = user.dailyGoal ?: "REGULER"
                        )
                    }
                }
                is AppResult.Error -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onDisplayNameChange(v: String) = _uiState.update { it.copy(displayName = v) }
    fun onInstitutionChange(v: String) = _uiState.update { it.copy(targetInstitution = v) }
    fun onPositionChange(v: String) = _uiState.update { it.copy(targetPosition = v) }
    fun onDailyGoalChange(v: String) = _uiState.update { it.copy(dailyGoal = v) }
    fun onReminderToggle(v: Boolean) = _uiState.update { it.copy(dailyReminderEnabled = v) }

    fun saveProfile(onSaved: () -> Unit) {
        val state = _uiState.value
        if (state.displayName.isBlank()) {
            _uiState.update { it.copy(snackbarMessage = "Nama lengkap wajib diisi.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            when (
                val res = repository.updateProfile(
                    UpdateProfileInput(
                        displayName = state.displayName.trim(),
                        targetInstitution = state.targetInstitution.trim(),
                        targetPosition = state.targetPosition.trim(),
                        dailyGoal = state.dailyGoal,
                        dailyReminderEnabled = state.dailyReminderEnabled
                    )
                )
            ) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isSaving = false, snackbarMessage = "Profil berhasil disimpan!") }
                    onSaved()
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isSaving = false, snackbarMessage = res.message)
                }
            }
        }
    }

    fun openDeleteModal() = _uiState.update { it.copy(showDeleteModal = true, deleteConfirmationText = "") }
    fun closeDeleteModal() = _uiState.update { it.copy(showDeleteModal = false, deleteConfirmationText = "") }
    fun onDeleteConfirmTextChange(v: String) = _uiState.update { it.copy(deleteConfirmationText = v) }

    fun confirmDeleteAccount(onDeleted: () -> Unit) {
        val text = _uiState.value.deleteConfirmationText
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            when (val res = repository.deleteAccount(text)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isDeleting = false, showDeleteModal = false) }
                    onDeleted()
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isDeleting = false, snackbarMessage = res.message)
                }
            }
        }
    }

    fun consumeSnackbar() = _uiState.update { it.copy(snackbarMessage = null) }
}
