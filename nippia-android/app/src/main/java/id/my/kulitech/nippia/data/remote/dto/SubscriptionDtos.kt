package id.my.kulitech.nippia.data.remote.dto

import id.my.kulitech.nippia.domain.model.ActiveSubscriptionSummary
import id.my.kulitech.nippia.domain.model.PauseSubscriptionResult
import id.my.kulitech.nippia.domain.model.ProUsageStats
import id.my.kulitech.nippia.domain.model.SubscriptionCatalog
import id.my.kulitech.nippia.domain.model.SubscriptionHistoryOverview
import id.my.kulitech.nippia.domain.model.SubscriptionPlan
import id.my.kulitech.nippia.domain.model.SubscriptionTransaction
import id.my.kulitech.nippia.domain.model.VerifiedSubscriptionReceipt
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionPlanDto(
    val productId: String,
    val title: String,
    val subtitle: String,
    val priceIdr: Int,
    val originalPriceIdr: Int? = null,
    val durationMonths: Int = 1,
    val trialDays: Int = 0,
    val badgeText: String? = null,
    val perMonthLabel: String = "",
    val isPopular: Boolean = false
) {
    fun toDomain() = SubscriptionPlan(
        productId = productId,
        title = title,
        subtitle = subtitle,
        priceIdr = priceIdr,
        originalPriceIdr = originalPriceIdr,
        durationMonths = durationMonths,
        trialDays = trialDays,
        badgeText = badgeText,
        perMonthLabel = perMonthLabel,
        isPopular = isPopular
    )
}

@Serializable
data class SubscriptionCatalogDataDto(
    val isPro: Boolean = false,
    val plans: List<SubscriptionPlanDto> = emptyList()
) {
    fun toDomain() = SubscriptionCatalog(
        isPro = isPro,
        plans = plans.map { it.toDomain() }
    )
}

@Serializable
data class VerifySubscriptionRequestDto(
    val productId: String,
    val purchaseToken: String,
    val paymentMethod: String
)

@Serializable
data class VerifiedSubscriptionReceiptDto(
    val subscriptionId: String,
    val productId: String,
    val planTitle: String,
    val status: String,
    val isTrial: Boolean = false,
    val expiryTime: String,
    val orderId: String,
    val paymentMethod: String,
    val email: String,
    val displayName: String
) {
    fun toDomain() = VerifiedSubscriptionReceipt(
        subscriptionId = subscriptionId,
        productId = productId,
        planTitle = planTitle,
        status = status,
        isTrial = isTrial,
        expiryTime = expiryTime,
        orderId = orderId,
        paymentMethod = paymentMethod,
        email = email,
        displayName = displayName
    )
}

@Serializable
data class RestoreSubscriptionDataDto(
    val restored: Boolean = false
)

@Serializable
data class ActiveSubscriptionSummaryDto(
    val id: String,
    val productId: String,
    val planTitle: String,
    val status: String,
    val isTrial: Boolean = false,
    val daysRemaining: Int = 0,
    val startDate: String = "",
    val expiryTime: String = "",
    val autoRenew: Boolean = true
) {
    fun toDomain() = ActiveSubscriptionSummary(
        id = id,
        productId = productId,
        planTitle = planTitle,
        status = status,
        isTrial = isTrial,
        daysRemaining = daysRemaining,
        startDate = startDate,
        expiryTime = expiryTime,
        autoRenew = autoRenew
    )
}

@Serializable
data class ProUsageStatsDto(
    val tryoutsTaken: Int = 0,
    val mistakesResolved: Int = 0,
    val offlineQuestionsSynced: Int = 240
) {
    fun toDomain() = ProUsageStats(
        tryoutsTaken = tryoutsTaken,
        mistakesResolved = mistakesResolved,
        offlineQuestionsSynced = offlineQuestionsSynced
    )
}

@Serializable
data class SubscriptionTransactionDto(
    val id: String,
    val title: String,
    val orderId: String,
    val amountIdr: Int,
    val statusLabel: String,
    val createdAt: String
) {
    fun toDomain() = SubscriptionTransaction(
        id = id,
        title = title,
        orderId = orderId,
        amountIdr = amountIdr,
        statusLabel = statusLabel,
        createdAt = createdAt
    )
}

@Serializable
data class SubscriptionHistoryDataDto(
    val activeSubscription: ActiveSubscriptionSummaryDto? = null,
    val usageStats: ProUsageStatsDto = ProUsageStatsDto(),
    val transactions: List<SubscriptionTransactionDto> = emptyList()
) {
    fun toDomain() = SubscriptionHistoryOverview(
        activeSubscription = activeSubscription?.toDomain(),
        usageStats = usageStats.toDomain(),
        transactions = transactions.map { it.toDomain() }
    )
}

@Serializable
data class PauseSubscriptionRequestDto(
    val durationDays: Int
)

@Serializable
data class PauseSubscriptionDataDto(
    val subscriptionId: String,
    val status: String,
    val pauseDays: Int,
    val resumeDate: String,
    val expiryTime: String
) {
    fun toDomain() = PauseSubscriptionResult(
        subscriptionId = subscriptionId,
        status = status,
        pauseDays = pauseDays,
        resumeDate = resumeDate,
        expiryTime = expiryTime
    )
}

@Serializable
data class CancelSubscriptionDataDto(
    val subscriptionId: String,
    val status: String,
    val accessUntil: String
)
