package id.my.kulitech.nippia.data.remote.dto

import id.my.kulitech.nippia.domain.model.GamificationDetailStatus
import id.my.kulitech.nippia.domain.model.HeartRefillResult
import id.my.kulitech.nippia.domain.model.MistakeAttemptResult
import id.my.kulitech.nippia.domain.model.MistakeBankItem
import id.my.kulitech.nippia.domain.model.MistakeBankOverview
import id.my.kulitech.nippia.domain.model.QuizOption
import id.my.kulitech.nippia.domain.model.QuizQuestionType
import id.my.kulitech.nippia.domain.model.StreakFreezePurchaseResult
import id.my.kulitech.nippia.domain.model.StreakWeekDay
import id.my.kulitech.nippia.domain.model.UnitStudyGuide
import kotlinx.serialization.Serializable

@Serializable
data class UnitStudyGuideDto(
    val unitId: String,
    val title: String,
    val sectionTitle: String = "",
    val categoryCode: String = "TWK",
    val categoryName: String = "",
    val subTopic: String = "",
    val xpReward: Int = 25,
    val questionCount: Int = 7,
    val readMinutes: Int = 3,
    val guideMarkdown: String = ""
) {
    fun toDomain() = UnitStudyGuide(
        unitId = unitId,
        title = title,
        sectionTitle = sectionTitle,
        categoryCode = categoryCode,
        categoryName = categoryName,
        subTopic = subTopic,
        xpReward = xpReward,
        questionCount = questionCount,
        readMinutes = readMinutes,
        guideMarkdown = guideMarkdown
    )
}

@Serializable
data class MistakeOptionDto(
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
data class MistakeBankItemDto(
    val id: String,
    val questionId: String,
    val categoryCode: String,
    val subTopic: String,
    val type: String,
    val contentText: String,
    val imageUrl: String? = null,
    val explanation: String = "",
    val wrongCount: Int = 1,
    val isResolved: Boolean = false,
    val updatedAt: String = "",
    val options: List<MistakeOptionDto> = emptyList()
) {
    fun toDomain() = MistakeBankItem(
        id = id,
        questionId = questionId,
        categoryCode = categoryCode,
        subTopic = subTopic,
        type = QuizQuestionType.fromApi(type),
        contentText = contentText,
        imageUrl = imageUrl,
        explanation = explanation,
        wrongCount = wrongCount,
        isResolved = isResolved,
        updatedAt = updatedAt,
        options = options.map { it.toDomain() }
    )
}

@Serializable
data class MistakeBankDataDto(
    val unresolvedCount: Int = 0,
    val resolvedCount: Int = 0,
    val remedialRatePercent: Int = 100,
    val dailyTarget: Int = 5,
    val items: List<MistakeBankItemDto> = emptyList()
) {
    fun toDomain() = MistakeBankOverview(
        unresolvedCount = unresolvedCount,
        resolvedCount = resolvedCount,
        remedialRatePercent = remedialRatePercent,
        dailyTarget = dailyTarget,
        items = items.map { it.toDomain() }
    )
}

@Serializable
data class MistakeAttemptRequestDto(
    val selectedOptionId: String
)

@Serializable
data class MistakeAttemptDataDto(
    val questionId: String,
    val isCorrect: Boolean,
    val earnedScore: Int,
    val isResolved: Boolean,
    val xpEarned: Int,
    val explanation: String
) {
    fun toDomain() = MistakeAttemptResult(
        questionId = questionId,
        isCorrect = isCorrect,
        earnedScore = earnedScore,
        isResolved = isResolved,
        xpEarned = xpEarned,
        explanation = explanation
    )
}

@Serializable
data class ClearMistakesDataDto(
    val deletedCount: Int = 0
)

@Serializable
data class StreakWeekDayDto(
    val dayLabel: String,
    val active: Boolean,
    val isToday: Boolean
) {
    fun toDomain() = StreakWeekDay(dayLabel = dayLabel, active = active, isToday = isToday)
}

@Serializable
data class GamificationDetailDto(
    val totalXp: Int = 0,
    val weeklyXp: Int = 0,
    val gems: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val streakFreezes: Int = 0,
    val maxStreakFreezes: Int = 2,
    val hearts: Int = 5,
    val maxHearts: Int = 5,
    val nextHeartRegenSeconds: Int = 0,
    val weekDays: List<StreakWeekDayDto> = emptyList()
) {
    fun toDomain() = GamificationDetailStatus(
        totalXp = totalXp,
        weeklyXp = weeklyXp,
        gems = gems,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        streakFreezes = streakFreezes,
        maxStreakFreezes = maxStreakFreezes,
        hearts = hearts,
        maxHearts = maxHearts,
        nextHeartRegenSeconds = nextHeartRegenSeconds,
        weekDays = weekDays.map { it.toDomain() }
    )
}

@Serializable
data class GamificationMethodRequestDto(
    val method: String
)

@Serializable
data class StreakFreezeDataDto(
    val streakFreezes: Int,
    val maxStreakFreezes: Int = 2,
    val gems: Int
) {
    fun toDomain() = StreakFreezePurchaseResult(
        streakFreezes = streakFreezes,
        maxStreakFreezes = maxStreakFreezes,
        gems = gems
    )
}

@Serializable
data class HeartRefillDataDto(
    val hearts: Int,
    val maxHearts: Int = 5,
    val gems: Int
) {
    fun toDomain() = HeartRefillResult(
        hearts = hearts,
        maxHearts = maxHearts,
        gems = gems
    )
}
