package id.my.kulitech.nippia.domain.model

data class UpdateProfileInput(
    val displayName: String,
    val targetInstitution: String,
    val targetPosition: String,
    val dailyGoal: String,
    val dailyReminderEnabled: Boolean
)

data class TryoutHistorySummary(
    val totalAttempts: Int,
    val avgTotalScore: Int,
    val passRatePercent: Int
)

data class TryoutHistoryItem(
    val attemptId: String,
    val packageId: String,
    val title: String,
    val type: TryoutType,
    val twkScore: Int,
    val tiuScore: Int,
    val tkpScore: Int,
    val totalScore: Int,
    val isPassed: Boolean,
    val submittedAt: String
)

data class TryoutHistoryOverview(
    val summary: TryoutHistorySummary,
    val attempts: List<TryoutHistoryItem>
)

data class BookmarkItem(
    val id: String,
    val itemType: String, // "QUESTION" | "GUIDE"
    val targetId: String,
    val categoryCode: String,
    val title: String,
    val snippet: String,
    val personalNote: String?,
    val createdAt: String
)

data class BookmarkOverview(
    val questionsCount: Int,
    val guidesCount: Int,
    val items: List<BookmarkItem>
)

data class PassingGradeRule(
    val category: String,
    val minScore: Int,
    val maxScore: Int,
    val questionCount: Int
)

data class FaqItem(
    val id: String,
    val title: String,
    val answer: String
)

data class FaqCatalog(
    val passingGrades: List<PassingGradeRule>,
    val faqs: List<FaqItem>
)
