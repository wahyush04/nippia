package id.my.kulitech.nippia.domain.model

data class CatStatsSummary(
    val totalAttempts: Int,
    val avgTotalScore: Int,
    val passRatePercent: Double,
    val avgTwkScore: Int,
    val avgTiuScore: Int,
    val avgTkpScore: Int
)

data class ProfileStatsOverview(
    val user: UserProfile,
    val gamification: GamificationSummary,
    val catStats: CatStatsSummary,
    val pendingMistakesCount: Int,
    val completedUnitsCount: Int
)
