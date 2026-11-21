package id.my.kulitech.nippia.domain.model

data class SubscriptionPlan(
    val productId: String,
    val title: String,
    val subtitle: String,
    val priceIdr: Int,
    val originalPriceIdr: Int?,
    val durationMonths: Int,
    val trialDays: Int,
    val badgeText: String?,
    val perMonthLabel: String,
    val isPopular: Boolean
)

data class ActiveSubscriptionSummary(
    val id: String,
    val productId: String,
    val planTitle: String,
    val status: String,
    val isTrial: Boolean,
    val daysRemaining: Int,
    val startDate: String,
    val expiryTime: String,
    val autoRenew: Boolean
)

data class SubscriptionCatalog(
    val isPro: Boolean,
    val plans: List<SubscriptionPlan>
)

data class VerifiedSubscriptionReceipt(
    val subscriptionId: String,
    val productId: String,
    val planTitle: String,
    val status: String,
    val isTrial: Boolean,
    val expiryTime: String,
    val orderId: String,
    val paymentMethod: String,
    val email: String,
    val displayName: String
)

data class ProUsageStats(
    val tryoutsTaken: Int,
    val mistakesResolved: Int,
    val offlineQuestionsSynced: Int
)

data class SubscriptionTransaction(
    val id: String,
    val title: String,
    val orderId: String,
    val amountIdr: Int,
    val statusLabel: String,
    val createdAt: String
)

data class SubscriptionHistoryOverview(
    val activeSubscription: ActiveSubscriptionSummary?,
    val usageStats: ProUsageStats,
    val transactions: List<SubscriptionTransaction>
)

data class PauseSubscriptionResult(
    val subscriptionId: String,
    val status: String,
    val pauseDays: Int,
    val resumeDate: String,
    val expiryTime: String
)
