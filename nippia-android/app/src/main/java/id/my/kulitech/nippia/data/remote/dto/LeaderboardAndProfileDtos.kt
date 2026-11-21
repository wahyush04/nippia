package id.my.kulitech.nippia.data.remote.dto

import id.my.kulitech.nippia.domain.model.CatStatsSummary
import id.my.kulitech.nippia.domain.model.ClaimLeagueRewardResult
import id.my.kulitech.nippia.domain.model.GamificationSummary
import id.my.kulitech.nippia.domain.model.LeaderboardEntry
import id.my.kulitech.nippia.domain.model.LeaderboardZone
import id.my.kulitech.nippia.domain.model.LeagueOutcomeInfo
import id.my.kulitech.nippia.domain.model.LeagueTier
import id.my.kulitech.nippia.domain.model.ProfileStatsOverview
import id.my.kulitech.nippia.domain.model.QuizOption
import id.my.kulitech.nippia.domain.model.QuizQuestionType
import id.my.kulitech.nippia.domain.model.RescueQuizQuestion
import id.my.kulitech.nippia.domain.model.RescueQuizSession
import id.my.kulitech.nippia.domain.model.RescueQuizSubmitResult
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


@Serializable
data class RescueQuizOptionDto(
    val id: String,
    val label: String,
    val optionText: String,
    val imageUrl: String? = null,
    val score: Int = 0
) {
    fun toDomain() = QuizOption(
        id = id,
        label = label,
        text = optionText,
        imageUrl = imageUrl,
        score = score
    )
}

@Serializable
data class RescueQuizQuestionDto(
    val id: String,
    val categoryCode: String,
    val subTopic: String,
    val type: String,
    val contentText: String,
    val explanation: String = "",
    val options: List<RescueQuizOptionDto> = emptyList()
) {
    fun toDomain() = RescueQuizQuestion(
        id = id,
        categoryCode = categoryCode,
        subTopic = subTopic,
        type = QuizQuestionType.fromApi(type),
        contentText = contentText,
        explanation = explanation,
        options = options.map { it.toDomain() }
    )
}

@Serializable
data class RescueQuizDataDto(
    val missionTitle: String = "Misi Penyelamat Liga",
    val leagueTier: String = "GOLD",
    val xpBonus: Int = 50,
    val gemsBonus: Int = 10,
    val timeLimitSeconds: Int = 180,
    val currentRank: Int = 28,
    val targetSafeRank: Int = 25,
    val currentXp: Int = 435,
    val targetSafeXp: Int = 485,
    val rivalName: String = "Gilang R.",
    val questions: List<RescueQuizQuestionDto> = emptyList()
) {
    fun toDomain() = RescueQuizSession(
        missionTitle = missionTitle,
        leagueTier = LeagueTier.fromApi(leagueTier),
        xpBonus = xpBonus,
        gemsBonus = gemsBonus,
        timeLimitSeconds = timeLimitSeconds,
        currentRank = currentRank,
        targetSafeRank = targetSafeRank,
        currentXp = currentXp,
        targetSafeXp = targetSafeXp,
        rivalName = rivalName,
        questions = questions.map { it.toDomain() }
    )
}

@Serializable
data class SubmitRescueQuizRequestDto(
    val answers: List<AnswerSubmissionDto>
)

@Serializable
data class RescueQuizSubmitDataDto(
    val previousRank: Int = 28,
    val newRank: Int = 25,
    val previousXp: Int = 435,
    val newXp: Int = 485,
    val xpEarned: Int = 50,
    val gemsEarned: Int = 10,
    val currentStreak: Int = 14,
    val correctCount: Int = 5,
    val totalQuestions: Int = 5,
    val leagueTier: String = "GOLD",
    val escapedDemotion: Boolean = true
) {
    fun toDomain() = RescueQuizSubmitResult(
        previousRank = previousRank,
        newRank = newRank,
        previousXp = previousXp,
        newXp = newXp,
        xpEarned = xpEarned,
        gemsEarned = gemsEarned,
        currentStreak = currentStreak,
        correctCount = correctCount,
        totalQuestions = totalQuestions,
        leagueTier = LeagueTier.fromApi(leagueTier),
        escapedDemotion = escapedDemotion
    )
}

@Serializable
data class LeagueOutcomeDataDto(
    val leagueTier: String = "SAPPHIRE",
    val tierName: String = "Safir",
    val tierIndex: Int = 4,
    val outcome: String = "PROMOTED",
    val rank: Int = 4,
    val weeklyXp: Int = 1290,
    val currentStreak: Int = 14,
    val rewardGems: Int = 500,
    val rewardFreezes: Int = 3,
    val weakestSubTopic: String = "TIU Numerik",
    val xpGapToSafe: Int = 0
) {
    fun toDomain() = LeagueOutcomeInfo(
        leagueTier = LeagueTier.fromApi(leagueTier),
        tierName = tierName,
        tierIndex = tierIndex,
        outcome = outcome,
        rank = rank,
        weeklyXp = weeklyXp,
        currentStreak = currentStreak,
        rewardGems = rewardGems,
        rewardFreezes = rewardFreezes,
        weakestSubTopic = weakestSubTopic,
        xpGapToSafe = xpGapToSafe
    )
}

@Serializable
data class ClaimLeagueOutcomeRequestDto(
    val outcome: String
)

@Serializable
data class ClaimLeagueRewardDataDto(
    val gems: Int = 500,
    val streakFreezes: Int = 3,
    val leagueTier: String = "SAPPHIRE"
) {
    fun toDomain() = ClaimLeagueRewardResult(
        gems = gems,
        streakFreezes = streakFreezes,
        leagueTier = LeagueTier.fromApi(leagueTier)
    )
}
