package id.my.kulitech.nippia.data.remote.dto

import id.my.kulitech.nippia.domain.model.BookmarkItem
import id.my.kulitech.nippia.domain.model.BookmarkOverview
import id.my.kulitech.nippia.domain.model.FaqCatalog
import id.my.kulitech.nippia.domain.model.FaqItem
import id.my.kulitech.nippia.domain.model.PassingGradeRule
import id.my.kulitech.nippia.domain.model.TryoutHistoryItem
import id.my.kulitech.nippia.domain.model.TryoutHistoryOverview
import id.my.kulitech.nippia.domain.model.TryoutHistorySummary
import id.my.kulitech.nippia.domain.model.TryoutType
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequestDto(
    val displayName: String,
    val targetInstitution: String,
    val targetPosition: String,
    val dailyGoal: String,
    val dailyReminderEnabled: Boolean
)

@Serializable
data class DeleteAccountRequestDto(
    val confirmationText: String
)

@Serializable
data class DeleteAccountDataDto(
    val deleted: Boolean = true
)

@Serializable
data class TryoutHistorySummaryDto(
    val totalAttempts: Int = 0,
    val avgTotalScore: Int = 0,
    val passRatePercent: Int = 0
) {
    fun toDomain() = TryoutHistorySummary(totalAttempts, avgTotalScore, passRatePercent)
}

@Serializable
data class TryoutHistoryItemDto(
    val attemptId: String,
    val packageId: String,
    val title: String,
    val type: String = "FULL_CAT",
    val twkScore: Int = 0,
    val tiuScore: Int = 0,
    val tkpScore: Int = 0,
    val totalScore: Int = 0,
    val isPassed: Boolean = false,
    val submittedAt: String = ""
) {
    fun toDomain() = TryoutHistoryItem(
        attemptId = attemptId,
        packageId = packageId,
        title = title,
        type = TryoutType.fromApi(type),
        twkScore = twkScore,
        tiuScore = tiuScore,
        tkpScore = tkpScore,
        totalScore = totalScore,
        isPassed = isPassed,
        submittedAt = submittedAt
    )
}

@Serializable
data class TryoutHistoryDataDto(
    val summary: TryoutHistorySummaryDto = TryoutHistorySummaryDto(),
    val attempts: List<TryoutHistoryItemDto> = emptyList()
) {
    fun toDomain() = TryoutHistoryOverview(
        summary = summary.toDomain(),
        attempts = attempts.map { it.toDomain() }
    )
}

@Serializable
data class BookmarkItemDto(
    val id: String,
    val itemType: String,
    val targetId: String,
    val categoryCode: String = "TWK",
    val title: String,
    val snippet: String,
    val personalNote: String? = null,
    val createdAt: String = ""
) {
    fun toDomain() = BookmarkItem(id, itemType, targetId, categoryCode, title, snippet, personalNote, createdAt)
}

@Serializable
data class BookmarkOverviewDataDto(
    val questionsCount: Int = 0,
    val guidesCount: Int = 0,
    val items: List<BookmarkItemDto> = emptyList()
) {
    fun toDomain() = BookmarkOverview(
        questionsCount = questionsCount,
        guidesCount = guidesCount,
        items = items.map { it.toDomain() }
    )
}

@Serializable
data class UpsertBookmarkRequestDto(
    val itemType: String,
    val targetId: String,
    val categoryCode: String,
    val title: String,
    val snippet: String,
    val personalNote: String? = null
)

@Serializable
data class PassingGradeRuleDto(
    val category: String,
    val minScore: Int,
    val maxScore: Int,
    val questionCount: Int
) {
    fun toDomain() = PassingGradeRule(category, minScore, maxScore, questionCount)
}

@Serializable
data class FaqItemDto(
    val id: String,
    val title: String,
    val answer: String
) {
    fun toDomain() = FaqItem(id, title, answer)
}

@Serializable
data class FaqCatalogDataDto(
    val passingGrades: List<PassingGradeRuleDto> = emptyList(),
    val faqs: List<FaqItemDto> = emptyList()
) {
    fun toDomain() = FaqCatalog(
        passingGrades = passingGrades.map { it.toDomain() },
        faqs = faqs.map { it.toDomain() }
    )
}

@Serializable
data class ReportQuestionRequestDto(
    val questionId: String? = null,
    val category: String,
    val description: String
)

@Serializable
data class ReportQuestionDataDto(
    val reportId: String = "",
    val status: String = "RECEIVED",
    val message: String = "Laporan diterima."
)
