package id.my.kulitech.nippia.data.remote.dto

import id.my.kulitech.nippia.domain.model.CatAnalysisReport
import id.my.kulitech.nippia.domain.model.QuestionPresentationParser
import id.my.kulitech.nippia.domain.model.QuizOption
import id.my.kulitech.nippia.domain.model.QuizQuestionType
import id.my.kulitech.nippia.domain.model.ScheduledTryoutEvent
import id.my.kulitech.nippia.domain.model.SubTopicBreakdownItem
import id.my.kulitech.nippia.domain.model.SubtestAnswerStats
import id.my.kulitech.nippia.domain.model.SubtestGradeResult
import id.my.kulitech.nippia.domain.model.TkpAnswerStats
import id.my.kulitech.nippia.domain.model.TryoutAttemptReview
import id.my.kulitech.nippia.domain.model.TryoutAttemptSummary
import id.my.kulitech.nippia.domain.model.TryoutCatalog
import id.my.kulitech.nippia.domain.model.TryoutLobbyInfo
import id.my.kulitech.nippia.domain.model.TryoutPackageItem
import id.my.kulitech.nippia.domain.model.TryoutQuestion
import id.my.kulitech.nippia.domain.model.TryoutReviewItem
import id.my.kulitech.nippia.domain.model.TryoutSession
import id.my.kulitech.nippia.domain.model.TryoutSubmitResult
import id.my.kulitech.nippia.domain.model.TryoutSubtestStats
import id.my.kulitech.nippia.domain.model.TryoutType
import kotlinx.serialization.Serializable

@Serializable
data class TryoutAttemptSummaryDto(
    val id: String,
    val twkScore: Int,
    val tiuScore: Int,
    val tkpScore: Int,
    val totalScore: Int,
    val isPassed: Boolean,
    val submittedAt: String? = null
) {
    fun toDomain() = TryoutAttemptSummary(
        id = id,
        twkScore = twkScore,
        tiuScore = tiuScore,
        tkpScore = tkpScore,
        totalScore = totalScore,
        isPassed = isPassed,
        submittedAt = submittedAt
    )
}

@Serializable
data class TryoutPackageDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val type: String,
    val durationMinutes: Int,
    val totalQuestions: Int,
    val isProOnly: Boolean = false,
    val lastAttempt: TryoutAttemptSummaryDto? = null
) {
    fun toDomain() = TryoutPackageItem(
        id = id,
        title = title,
        description = description.orEmpty(),
        type = TryoutType.fromApi(type),
        durationMinutes = durationMinutes,
        totalQuestions = totalQuestions,
        isProOnly = isProOnly,
        lastAttempt = lastAttempt?.toDomain()
    )
}

@Serializable
data class ScheduledTryoutEventDto(
    val id: String,
    val title: String,
    val scheduleLabel: String,
    val participantsCount: Int,
    val durationMinutes: Int,
    val totalQuestions: Int
) {
    fun toDomain() = ScheduledTryoutEvent(
        id = id,
        title = title,
        scheduleLabel = scheduleLabel,
        participantsCount = participantsCount,
        durationMinutes = durationMinutes,
        totalQuestions = totalQuestions
    )
}

@Serializable
data class TryoutCatalogDataDto(
    val packages: List<TryoutPackageDto> = emptyList(),
    val scheduledEvent: ScheduledTryoutEventDto? = null
) {
    fun toDomain() = TryoutCatalog(
        packages = packages.map { it.toDomain() },
        scheduledEvent = scheduledEvent?.toDomain() ?: ScheduledTryoutEvent(
            id = "akbar-nasional-12",
            title = "Tryout Akbar Nasional SKD #12",
            scheduleLabel = "Sabtu, 29 Maret 2025 • 09.00 - 10.40 WIB",
            participantsCount = 1280,
            durationMinutes = 100,
            totalQuestions = 110
        )
    )
}

@Serializable
data class TryoutOptionDto(
    val id: String,
    val label: String,
    val optionText: String,
    val imageUrl: String? = null
) {
    fun toDomain() = QuizOption(
        id = id,
        label = label,
        text = optionText,
        imageUrl = imageUrl,
        score = 0
    )
}

@Serializable
data class TryoutQuestionDto(
    val id: String,
    val orderIndex: Int,
    val categoryCode: String,
    val subTopic: String,
    val type: String,
    val contentText: String,
    val imageUrl: String? = null,
    val options: List<TryoutOptionDto> = emptyList()
) {
    fun toDomain(): TryoutQuestion {
        val parsed = QuestionPresentationParser.parse(contentText)
        return TryoutQuestion(
            id = id,
            orderIndex = orderIndex,
            categoryCode = categoryCode,
            subTopic = subTopic,
            type = QuizQuestionType.fromApi(type),
            contentText = contentText,
            imageUrl = imageUrl,
            presentation = parsed.presentation,
            hint = parsed.hint,
            options = options.map { it.toDomain() }
        )
    }
}

@Serializable
data class StartTryoutDataDto(
    val attemptId: String,
    val packageId: String,
    val title: String = "Simulasi CAT BKN",
    val type: String = "FULL_CAT",
    val durationMinutes: Int = 100,
    val startedAt: String = "",
    val expiresAt: String = "",
    val questions: List<TryoutQuestionDto> = emptyList()
) {
    fun toDomain() = TryoutSession(
        attemptId = attemptId,
        packageId = packageId,
        title = title,
        type = TryoutType.fromApi(type),
        durationMinutes = durationMinutes,
        startedAt = startedAt,
        expiresAt = expiresAt,
        questions = questions.map { it.toDomain() }
    )
}

@Serializable
data class SubmitTryoutRequestDto(
    val answers: List<AnswerSubmissionDto>
)

@Serializable
data class SubtestGradeDto(
    val min: Int,
    val passed: Boolean
) {
    fun toDomain() = SubtestGradeResult(min = min, passed = passed)
}

@Serializable
data class PassingGradesDto(
    val twk: SubtestGradeDto = SubtestGradeDto(65, false),
    val tiu: SubtestGradeDto = SubtestGradeDto(80, false),
    val tkp: SubtestGradeDto = SubtestGradeDto(166, false)
)

@Serializable
data class SubTopicBreakdownDto(
    val categoryCode: String,
    val subTopic: String,
    val earnedScore: Int,
    val maxScore: Int,
    val accuracyPercent: Double
) {
    fun toDomain() = SubTopicBreakdownItem(
        categoryCode = categoryCode,
        subTopic = subTopic,
        earnedScore = earnedScore,
        maxScore = maxScore,
        accuracyPercent = accuracyPercent
    )
}

@Serializable
data class SubtestAnswerStatsDto(
    val questionCount: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val unansweredCount: Int = 0,
    val averageScore: Int = 75
) {
    fun toDomain() = SubtestAnswerStats(
        questionCount = questionCount,
        correctCount = correctCount,
        wrongCount = wrongCount,
        unansweredCount = unansweredCount,
        averageScore = averageScore
    )
}

@Serializable
data class TkpAnswerStatsDto(
    val questionCount: Int = 0,
    val point5Count: Int = 0,
    val point4Or3Count: Int = 0,
    val point2Or1Count: Int = 0,
    val unansweredCount: Int = 0,
    val averageScore: Int = 168
) {
    fun toDomain() = TkpAnswerStats(
        questionCount = questionCount,
        point5Count = point5Count,
        point4Or3Count = point4Or3Count,
        point2Or1Count = point2Or1Count,
        unansweredCount = unansweredCount,
        averageScore = averageScore
    )
}

@Serializable
data class TryoutSubtestStatsDto(
    val twk: SubtestAnswerStatsDto = SubtestAnswerStatsDto(),
    val tiu: SubtestAnswerStatsDto = SubtestAnswerStatsDto(averageScore = 90),
    val tkp: TkpAnswerStatsDto = TkpAnswerStatsDto()
) {
    fun toDomain() = TryoutSubtestStats(
        twk = twk.toDomain(),
        tiu = tiu.toDomain(),
        tkp = tkp.toDomain()
    )
}

@Serializable
data class CatAnalysisReportDto(
    val confidenceLevel: String = "TINGGI",
    val passProbabilityPercent: Int = 85,
    val recommendationText: String = "",
    val benchmarkNote: String = ""
) {
    fun toDomain() = CatAnalysisReport(
        confidenceLevel = confidenceLevel,
        passProbabilityPercent = passProbabilityPercent,
        recommendationText = recommendationText,
        benchmarkNote = benchmarkNote
    )
}

@Serializable
data class SubmitTryoutDataDto(
    val attemptId: String,
    val packageId: String,
    val twkScore: Int = 0,
    val tiuScore: Int = 0,
    val tkpScore: Int = 0,
    val totalScore: Int = 0,
    val nationalRank: Int = 1,
    val totalParticipants: Int = 1,
    val passingGrades: PassingGradesDto = PassingGradesDto(),
    val subtestStats: TryoutSubtestStatsDto = TryoutSubtestStatsDto(),
    val catAnalysis: CatAnalysisReportDto = CatAnalysisReportDto(),
    val isPassed: Boolean = false,
    val subTopicBreakdown: List<SubTopicBreakdownDto> = emptyList()
) {
    fun toDomain() = TryoutSubmitResult(
        attemptId = attemptId,
        packageId = packageId,
        twkScore = twkScore,
        tiuScore = tiuScore,
        tkpScore = tkpScore,
        totalScore = totalScore,
        nationalRank = nationalRank,
        totalParticipants = totalParticipants,
        twkGrade = passingGrades.twk.toDomain(),
        tiuGrade = passingGrades.tiu.toDomain(),
        tkpGrade = passingGrades.tkp.toDomain(),
        subtestStats = subtestStats.toDomain(),
        catAnalysis = catAnalysis.toDomain(),
        isPassed = isPassed,
        subTopicBreakdown = subTopicBreakdown.map { it.toDomain() }
    )
}

@Serializable
data class TryoutLobbyDataDto(
    val packageId: String,
    val title: String,
    val durationMinutes: Int,
    val totalQuestions: Int,
    val sessionToken: String,
    val participantsCount: Int
) {
    fun toDomain() = TryoutLobbyInfo(
        packageId = packageId,
        title = title,
        durationMinutes = durationMinutes,
        totalQuestions = totalQuestions,
        sessionToken = sessionToken,
        participantsCount = participantsCount
    )
}

@Serializable
data class StartDrillRequestDto(
    val categoryCode: String
)

@Serializable
data class ReviewOptionDto(
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
data class TryoutReviewItemDto(
    val questionId: String,
    val orderIndex: Int,
    val categoryCode: String,
    val subTopic: String,
    val type: String,
    val contentText: String,
    val explanation: String = "",
    val selectedOptionId: String? = null,
    val earnedScore: Int = 0,
    val bestOptionId: String? = null,
    val options: List<ReviewOptionDto> = emptyList()
) {
    fun toDomain() = TryoutReviewItem(
        questionId = questionId,
        orderIndex = orderIndex,
        categoryCode = categoryCode,
        subTopic = subTopic,
        type = QuizQuestionType.fromApi(type),
        contentText = contentText,
        explanation = explanation,
        selectedOptionId = selectedOptionId,
        earnedScore = earnedScore,
        bestOptionId = bestOptionId,
        options = options.map { it.toDomain() }
    )
}

@Serializable
data class TryoutReviewDataDto(
    val attemptId: String,
    val packageTitle: String,
    val totalScore: Int = 0,
    val twkScore: Int = 0,
    val tiuScore: Int = 0,
    val tkpScore: Int = 0,
    val isPassed: Boolean = false,
    val items: List<TryoutReviewItemDto> = emptyList()
) {
    fun toDomain() = TryoutAttemptReview(
        attemptId = attemptId,
        packageTitle = packageTitle,
        totalScore = totalScore,
        twkScore = twkScore,
        tiuScore = tiuScore,
        tkpScore = tkpScore,
        isPassed = isPassed,
        items = items.map { it.toDomain() }
    )
}
