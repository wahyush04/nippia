package id.my.kulitech.nippia.domain.model

data class UnitStudyGuide(
    val unitId: String,
    val title: String,
    val sectionTitle: String,
    val categoryCode: String,
    val categoryName: String,
    val subTopic: String,
    val xpReward: Int,
    val questionCount: Int,
    val readMinutes: Int,
    val guideMarkdown: String
)

data class MistakeBankItem(
    val id: String,
    val questionId: String,
    val categoryCode: String,
    val subTopic: String,
    val type: QuizQuestionType,
    val contentText: String,
    val imageUrl: String?,
    val explanation: String,
    val wrongCount: Int,
    val isResolved: Boolean,
    val updatedAt: String,
    val options: List<QuizOption>
)

data class MistakeBankOverview(
    val unresolvedCount: Int,
    val resolvedCount: Int,
    val remedialRatePercent: Int,
    val dailyTarget: Int,
    val items: List<MistakeBankItem>
)

data class MistakeAttemptResult(
    val questionId: String,
    val isCorrect: Boolean,
    val earnedScore: Int,
    val isResolved: Boolean,
    val xpEarned: Int,
    val explanation: String
)

data class StreakWeekDay(
    val dayLabel: String,
    val active: Boolean,
    val isToday: Boolean
)

data class GamificationDetailStatus(
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
    val weekDays: List<StreakWeekDay> = emptyList()
)

data class StreakFreezePurchaseResult(
    val streakFreezes: Int,
    val maxStreakFreezes: Int,
    val gems: Int
)

data class HeartRefillResult(
    val hearts: Int,
    val maxHearts: Int,
    val gems: Int
)
