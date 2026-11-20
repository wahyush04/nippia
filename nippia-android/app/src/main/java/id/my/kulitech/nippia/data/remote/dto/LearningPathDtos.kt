package id.my.kulitech.nippia.data.remote.dto

import id.my.kulitech.nippia.domain.model.GamificationSummary
import id.my.kulitech.nippia.domain.model.LearningCategoryPath
import id.my.kulitech.nippia.domain.model.LearningPathOverview
import id.my.kulitech.nippia.domain.model.LearningUnit
import id.my.kulitech.nippia.domain.model.LearningUnitStatus
import id.my.kulitech.nippia.domain.model.SkdCategoryCode
import kotlinx.serialization.Serializable

@Serializable
data class UnitProgressDto(
    val status: String,
    val bestScore: Int = 0,
    val attemptsCount: Int = 0,
    val completedAt: String? = null
)

@Serializable
data class LearningUnitDto(
    val id: String,
    val categoryId: String,
    val sectionTitle: String,
    val orderIndex: Int,
    val title: String,
    val subTopic: String,
    val guideMarkdown: String = "",
    val xpReward: Int = 15,
    val isProOnly: Boolean = false,
    val progress: UnitProgressDto
) {
    fun toDomain() = LearningUnit(
        id = id,
        categoryId = categoryId,
        sectionTitle = sectionTitle,
        orderIndex = orderIndex,
        title = title,
        subTopic = subTopic,
        guideMarkdown = guideMarkdown,
        xpReward = xpReward,
        isProOnly = isProOnly,
        status = LearningUnitStatus.fromApi(progress.status),
        bestScore = progress.bestScore
    )
}

@Serializable
data class LearningCategoryDto(
    val id: String,
    val code: String,
    val name: String,
    val passingGrade: Int,
    val units: List<LearningUnitDto> = emptyList()
) {
    fun toDomain() = LearningCategoryPath(
        id = id,
        code = SkdCategoryCode.fromCode(code),
        name = name,
        passingGrade = passingGrade,
        units = units.map { it.toDomain() }
    )
}

@Serializable
data class LearningPathGamificationDto(
    val totalXp: Int = 0,
    val weeklyXp: Int = 0,
    val gems: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val streakFreezes: Int = 0,
    val hearts: Int = 5,
    val leagueTier: String = "BRONZE",
    val isPro: Boolean = false
) {
    fun toDomain() = GamificationSummary(
        totalXp = totalXp,
        weeklyXp = weeklyXp,
        gems = gems,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        streakFreezes = streakFreezes,
        hearts = hearts,
        leagueTier = leagueTier
    )
}

@Serializable
data class LearningPathDataDto(
    val pendingMistakesCount: Int = 0,
    val gamification: LearningPathGamificationDto = LearningPathGamificationDto(),
    val categories: List<LearningCategoryDto> = emptyList()
) {
    fun toDomain() = LearningPathOverview(
        pendingMistakesCount = pendingMistakesCount,
        gamification = gamification.toDomain(),
        categories = categories.map { it.toDomain() }
    )
}
