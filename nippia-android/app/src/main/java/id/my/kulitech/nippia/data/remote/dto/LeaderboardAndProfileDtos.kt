package id.my.kulitech.nippia.data.remote.dto

import id.my.kulitech.nippia.domain.model.CatStatsSummary
import id.my.kulitech.nippia.domain.model.GamificationSummary
import id.my.kulitech.nippia.domain.model.LeaderboardEntry
import id.my.kulitech.nippia.domain.model.LeaderboardZone
import id.my.kulitech.nippia.domain.model.LeagueTier
import id.my.kulitech.nippia.domain.model.ProfileStatsOverview
import id.my.kulitech.nippia.domain.model.WeeklyLeaderboard
import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardEntryDto(
    val rank: Int,
    val userId: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val targetInstitution: String? = null,
    val weeklyXp: Int,
    val isCurrentUser: Boolean = false,
    val zone: String = "SAFE"
) {
    fun toDomain() = LeaderboardEntry(
        rank = rank,
        userId = userId,
        displayName = displayName,
        avatarUrl = avatarUrl,
        targetInstitution = targetInstitution,
        weeklyXp = weeklyXp,
        isCurrentUser = isCurrentUser,
        zone = LeaderboardZone.fromApi(zone)
    )
}

@Serializable
data class WeeklyLeaderboardDataDto(
    val leagueTier: String = "BRONZE",
    val promotionCutoffRank: Int = 10,
    val demotionCutoffRank: Int = 26,
    val myRank: Int = 1,
    val myWeeklyXp: Int = 0,
    val entries: List<LeaderboardEntryDto> = emptyList()
) {
    fun toDomain() = WeeklyLeaderboard(
        leagueTier = LeagueTier.fromApi(leagueTier),
        promotionCutoffRank = promotionCutoffRank,
        demotionCutoffRank = demotionCutoffRank,
        myRank = myRank,
        myWeeklyXp = myWeeklyXp,
        entries = entries.map { it.toDomain() }
    )
}

@Serializable
data class CatStatsDto(
    val totalAttempts: Int = 0,
    val avgTotalScore: Int = 0,
    val passRatePercent: Double = 0.0,
    val avgTwkScore: Int = 0,
    val avgTiuScore: Int = 0,
    val avgTkpScore: Int = 0
) {
    fun toDomain() = CatStatsSummary(
        totalAttempts = totalAttempts,
        avgTotalScore = avgTotalScore,
        passRatePercent = passRatePercent,
        avgTwkScore = avgTwkScore,
        avgTiuScore = avgTiuScore,
        avgTkpScore = avgTkpScore
    )
}

@Serializable
data class ProfileStatsDataDto(
    val user: UserDto,
    val gamification: GamificationDto? = null,
    val catStats: CatStatsDto = CatStatsDto(),
    val pendingMistakesCount: Int = 0,
    val completedUnitsCount: Int = 0
) {
    fun toDomain() = ProfileStatsOverview(
        user = user.toDomain(),
        gamification = gamification?.toDomain() ?: GamificationSummary(0, 0, 0, 0, 0, 0, 5, "BRONZE"),
        catStats = catStats.toDomain(),
        pendingMistakesCount = pendingMistakesCount,
        completedUnitsCount = completedUnitsCount
    )
}
