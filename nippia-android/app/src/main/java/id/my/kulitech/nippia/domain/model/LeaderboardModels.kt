package id.my.kulitech.nippia.domain.model

enum class LeagueTier(val label: String, val shortCode: String, val tierNumber: Int) {
    BRONZE("Liga Perunggu", "Prg", 1),
    SILVER("Liga Perak", "Prk", 2),
    GOLD("Liga Emas", "Emas", 3),
    SAPPHIRE("Liga Safir", "Sfr", 4),
    DIAMOND("Liga Berlian", "Brl", 5);

    companion object {
        fun fromApi(value: String): LeagueTier =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: BRONZE
    }
}

enum class LeaderboardZone {
    PROMOTION,
    SAFE,
    DEMOTION;

    companion object {
        fun fromApi(value: String): LeaderboardZone =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: SAFE
    }
}

data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val displayName: String,
    val avatarUrl: String?,
    val targetInstitution: String?,
    val weeklyXp: Int,
    val isCurrentUser: Boolean,
    val zone: LeaderboardZone
)

data class WeeklyLeaderboard(
    val leagueTier: LeagueTier,
    val promotionCutoffRank: Int,
    val demotionCutoffRank: Int,
    val myRank: Int,
    val myWeeklyXp: Int,
    val entries: List<LeaderboardEntry>
)

data class RescueQuizQuestion(
    val id: String,
    val categoryCode: String,
    val subTopic: String,
    val type: QuizQuestionType,
    val contentText: String,
    val explanation: String,
    val options: List<QuizOption>
)

data class RescueQuizSession(
    val missionTitle: String,
    val leagueTier: LeagueTier,
    val xpBonus: Int,
    val gemsBonus: Int,
    val timeLimitSeconds: Int,
    val currentRank: Int,
    val targetSafeRank: Int,
    val currentXp: Int,
    val targetSafeXp: Int,
    val rivalName: String,
    val questions: List<RescueQuizQuestion>
)

data class RescueQuizSubmitResult(
    val previousRank: Int,
    val newRank: Int,
    val previousXp: Int,
    val newXp: Int,
    val xpEarned: Int,
    val gemsEarned: Int,
    val currentStreak: Int,
    val correctCount: Int,
    val totalQuestions: Int,
    val leagueTier: LeagueTier,
    val escapedDemotion: Boolean
)

data class LeagueOutcomeInfo(
    val leagueTier: LeagueTier,
    val tierName: String,
    val tierIndex: Int,
    val outcome: String, // "PROMOTED", "DEMOTED", "STAYED"
    val rank: Int,
    val weeklyXp: Int,
    val currentStreak: Int,
    val rewardGems: Int,
    val rewardFreezes: Int,
    val weakestSubTopic: String,
    val xpGapToSafe: Int
)

data class ClaimLeagueRewardResult(
    val gems: Int,
    val streakFreezes: Int,
    val leagueTier: LeagueTier
)
