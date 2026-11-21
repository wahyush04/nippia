package id.my.kulitech.nippia.domain.model

enum class TryoutType {
    MINI_CAT,
    FULL_CAT;

    companion object {
        fun fromApi(value: String): TryoutType =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: MINI_CAT
    }
}

data class TryoutAttemptSummary(
    val id: String,
    val twkScore: Int,
    val tiuScore: Int,
    val tkpScore: Int,
    val totalScore: Int,
    val isPassed: Boolean,
    val submittedAt: String?
)

data class TryoutPackageItem(
    val id: String,
    val title: String,
    val description: String,
    val type: TryoutType,
    val durationMinutes: Int,
    val totalQuestions: Int,
    val isProOnly: Boolean,
    val lastAttempt: TryoutAttemptSummary?
)

data class ScheduledTryoutEvent(
    val id: String,
    val title: String,
    val scheduleLabel: String,
    val participantsCount: Int,
    val durationMinutes: Int,
    val totalQuestions: Int
)

data class TryoutCatalog(
    val packages: List<TryoutPackageItem>,
    val scheduledEvent: ScheduledTryoutEvent
)

data class TryoutQuestion(
    val id: String,
    val orderIndex: Int,
    val categoryCode: String,
    val subTopic: String,
    val type: QuizQuestionType,
    val contentText: String,
    val imageUrl: String?,
    val presentation: QuestionPresentation,
    val hint: String?,
    val options: List<QuizOption>
) {
    fun asQuizQuestion(): QuizQuestion = QuizQuestion(
        id = id,
        categoryCode = categoryCode,
        subTopic = subTopic,
        type = type,
        orderIndex = orderIndex,
        contentText = contentText,
        imageUrl = imageUrl,
        explanation = "",
        hint = hint,
        presentation = presentation,
        options = options
    )
}

data class TryoutSession(
    val attemptId: String,
    val packageId: String,
    val title: String,
    val type: TryoutType,
    val durationMinutes: Int,
    val startedAt: String,
    val expiresAt: String,
    val questions: List<TryoutQuestion>
)

data class SubtestGradeResult(
    val min: Int,
    val passed: Boolean
)

data class SubTopicBreakdownItem(
    val categoryCode: String,
    val subTopic: String,
    val earnedScore: Int,
    val maxScore: Int,
    val accuracyPercent: Double
)

data class TryoutSubmitResult(
    val attemptId: String,
    val packageId: String,
    val twkScore: Int,
    val tiuScore: Int,
    val tkpScore: Int,
    val totalScore: Int,
    val twkGrade: SubtestGradeResult,
    val tiuGrade: SubtestGradeResult,
    val tkpGrade: SubtestGradeResult,
    val isPassed: Boolean,
    val subTopicBreakdown: List<SubTopicBreakdownItem>
)
