package id.my.kulitech.nippia.domain.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.PauseSubscriptionResult
import id.my.kulitech.nippia.domain.model.SubscriptionCatalog
import id.my.kulitech.nippia.domain.model.SubscriptionHistoryOverview
import id.my.kulitech.nippia.domain.model.VerifiedSubscriptionReceipt

interface SubscriptionRepository {
    suspend fun getPlansCatalog(): AppResult<SubscriptionCatalog>
    suspend fun verifyPurchase(
        productId: String,
        purchaseToken: String,
        paymentMethod: String = "Google Play (GoPay / DANA)"
    ): AppResult<VerifiedSubscriptionReceipt>
    suspend fun restorePurchases(): AppResult<Boolean>
    suspend fun getSubscriptionHistory(): AppResult<SubscriptionHistoryOverview>
    suspend fun pauseSubscription(durationDays: Int): AppResult<PauseSubscriptionResult>
    suspend fun cancelSubscription(): AppResult<String>
}
