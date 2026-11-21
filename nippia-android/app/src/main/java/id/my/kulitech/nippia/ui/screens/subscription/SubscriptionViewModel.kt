package id.my.kulitech.nippia.ui.screens.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.SubscriptionHistoryOverview
import id.my.kulitech.nippia.domain.model.SubscriptionPlan
import id.my.kulitech.nippia.domain.model.VerifiedSubscriptionReceipt
import id.my.kulitech.nippia.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class SubscriptionUiState(
    val isLoading: Boolean = true,
    val isPro: Boolean = false,
    val plans: List<SubscriptionPlan> = emptyList(),
    val selectedProductId: String = "nippia_pro_3m",
    val isProcessingPurchase: Boolean = false,
    val verifiedReceipt: VerifiedSubscriptionReceipt? = null,
    val history: SubscriptionHistoryOverview? = null,
    val showPauseModal: Boolean = false,
    val selectedPauseDays: Int = 30,
    val showCancelModal: Boolean = false,
    val showCancelFinalDialog: Boolean = false,
    val snackbarMessage: String? = null
) {
    val selectedPlan: SubscriptionPlan?
        get() = plans.firstOrNull { it.productId == selectedProductId } ?: plans.firstOrNull()
}

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionUiState())
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()

    fun loadPlans() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val res = repository.getPlansCatalog()) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        isPro = res.data.isPro,
                        plans = res.data.plans,
                        selectedProductId = res.data.plans.firstOrNull { p -> p.isPopular }?.productId
                            ?: res.data.plans.firstOrNull()?.productId
                            ?: "nippia_pro_3m"
                    )
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isLoading = false, snackbarMessage = res.message)
                }
            }
        }
    }

    fun selectPlan(productId: String) {
        _uiState.update { it.copy(selectedProductId = productId) }
    }

    fun startPurchase(onSuccess: () -> Unit) {
        val productId = _uiState.value.selectedProductId
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingPurchase = true) }
            val token = "gp_tok_${UUID.randomUUID().toString().take(12)}"
            when (val res = repository.verifyPurchase(productId = productId, purchaseToken = token)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isProcessingPurchase = false,
                            isPro = true,
                            verifiedReceipt = res.data
                        )
                    }
                    onSuccess()
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isProcessingPurchase = false, snackbarMessage = res.message)
                }
            }
        }
    }

    fun restorePurchases() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingPurchase = true) }
            when (val res = repository.restorePurchases()) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        isProcessingPurchase = false,
                        isPro = res.data,
                        snackbarMessage = if (res.data) {
                            "Langganan Nippia Pro berhasil dipulihkan!"
                        } else {
                            "Tidak ditemukan pembelian aktif."
                        }
                    )
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isProcessingPurchase = false, snackbarMessage = res.message)
                }
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val res = repository.getSubscriptionHistory()) {
                is AppResult.Success -> _uiState.update {
                    it.copy(isLoading = false, history = res.data)
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isLoading = false, snackbarMessage = res.message)
                }
            }
        }
    }

    fun openPauseModal() {
        _uiState.update { it.copy(showPauseModal = true, showCancelModal = false) }
    }

    fun closePauseModal() {
        _uiState.update { it.copy(showPauseModal = false) }
    }

    fun selectPauseDays(days: Int) {
        _uiState.update { it.copy(selectedPauseDays = days) }
    }

    fun confirmPause() {
        val days = _uiState.value.selectedPauseDays
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingPurchase = true) }
            when (val res = repository.pauseSubscription(days)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isProcessingPurchase = false,
                            showPauseModal = false,
                            snackbarMessage = "Langganan dijeda ${res.data.pauseDays} hari."
                        )
                    }
                    loadHistory()
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isProcessingPurchase = false, snackbarMessage = res.message)
                }
            }
        }
    }

    fun openCancelModal() {
        _uiState.update { it.copy(showCancelModal = true, showPauseModal = false) }
    }

    fun closeCancelModal() {
        _uiState.update { it.copy(showCancelModal = false, showCancelFinalDialog = false) }
    }

    fun openCancelFinalDialog() {
        _uiState.update { it.copy(showCancelFinalDialog = true) }
    }

    fun confirmCancel() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingPurchase = true) }
            when (val res = repository.cancelSubscription()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isProcessingPurchase = false,
                            showCancelModal = false,
                            showCancelFinalDialog = false,
                            snackbarMessage = "Perpanjangan otomatis dibatalkan."
                        )
                    }
                    loadHistory()
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isProcessingPurchase = false, snackbarMessage = res.message)
                }
            }
        }
    }

    fun consumeSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
