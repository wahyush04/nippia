package id.my.kulitech.nippia.data.remote.dto

import id.my.kulitech.nippia.domain.model.CpnsFormation
import id.my.kulitech.nippia.domain.model.DiagnosticOption
import id.my.kulitech.nippia.domain.model.DiagnosticQuestion
import id.my.kulitech.nippia.domain.model.PassingGradeInfo
import kotlinx.serialization.Serializable

@Serializable
data class PassingGradeDto(
    val twk: Int,
    val tiu: Int,
    val tkp: Int,
    val totalMin: Int
)

@Serializable
data class CpnsFormationDto(
    val id: String,
    val institution: String,
    val position: String,
    val category: String,
    val qualification: String,
    val badge: String? = null,
    val passingGrade: PassingGradeDto,
    val safeTargetScore: Int,
    val competitionRatio: String
) {
    fun toDomain() = CpnsFormation(
        id = id,
        institution = institution,
        position = position,
        category = category,
        qualification = qualification,
        badge = badge,
        passingGrade = PassingGradeInfo(
            twk = passingGrade.twk,
            tiu = passingGrade.tiu,
            tkp = passingGrade.tkp,
            totalMin = passingGrade.totalMin
        ),
        safeTargetScore = safeTargetScore,
        competitionRatio = competitionRatio
    )
}

@Serializable
data class FormationsDataDto(
    val formations: List<CpnsFormationDto>
)

@Serializable
data class DiagnosticOptionDto(
    val label: String,
    val text: String,
    val score: Int
)

@Serializable
data class DiagnosticQuestionDto(
    val id: String,
    val categoryCode: String,
    val topicLabel: String,
    val questionType: String,
    val contentText: String,
    val explanation: String,
    val options: List<DiagnosticOptionDto>
) {
    fun toDomain() = DiagnosticQuestion(
        id = id,
        categoryCode = categoryCode,
        topicLabel = topicLabel,
        questionType = questionType,
        contentText = contentText,
        explanation = explanation,
        options = options.map { DiagnosticOption(it.label, it.text, it.score) }
    )
}

@Serializable
data class DiagnosticQuestionsDataDto(
    val questions: List<DiagnosticQuestionDto>
)

@Serializable
data class DiagnosticScoreRequest(
    val twkCorrect: Boolean,
    val tiuCorrect: Boolean,
    val tkpScore: Int
)

@Serializable
data class SubmitOnboardingRequest(
    val targetInstitution: String,
    val targetPosition: String,
    val dailyGoal: String,
    val dailyReminderEnabled: Boolean,
    val dailyReminderTime: String,
    val diagnosticCompleted: Boolean,
    val diagnosticScore: DiagnosticScoreRequest? = null
)

@Serializable
data class BonusAwardedDto(
    val xp: Int = 0,
    val gems: Int = 0
)

@Serializable
data class SubmitOnboardingDataDto(
    val onboardingCompleted: Boolean = true,
    val bonusAwarded: BonusAwardedDto = BonusAwardedDto()
)
