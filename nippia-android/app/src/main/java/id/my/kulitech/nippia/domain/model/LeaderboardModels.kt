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
