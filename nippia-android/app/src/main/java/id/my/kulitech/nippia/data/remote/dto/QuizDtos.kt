package id.my.kulitech.nippia.data.remote.dto

import id.my.kulitech.nippia.domain.model.QuestionPresentationParser
import id.my.kulitech.nippia.domain.model.QuizOption
import id.my.kulitech.nippia.domain.model.QuizQuestion
import id.my.kulitech.nippia.domain.model.QuizQuestionType
import id.my.kulitech.nippia.domain.model.QuizUnitSession
import id.my.kulitech.nippia.domain.model.UnitCompletionResult
import kotlinx.serialization.Serializable

@Serializable
data class QuizUnitDto(
    val id: String,
    val categoryId: String = "",
    val title: String,
    val subTopic: String = "",
    val guideMarkdown: String = "",
    val xpReward: Int = 15,
    val isProOnly: Boolean = false
)

@Serializable
data class QuizOptionDto(
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
data class QuizQuestionDto(
    val id: String,
    val categoryCode: String = "TWK",
    val subTopic: String = "",
    val type: String = "MULTIPLE_CHOICE",
    val orderIndex: Int = 1,
    val contentText: String,
    val imageUrl: String? = null,
    val explanation: String = "",
    val options: List<QuizOptionDto> = emptyList()
) {
    fun toDomain(): QuizQuestion {
        val parsed = QuestionPresentationParser.parse(contentText)
        return QuizQuestion(
            id = id,
            categoryCode = categoryCode,
            subTopic = subTopic,
            type = QuizQuestionType.fromApi(type),
            orderIndex = orderIndex,
            contentText = contentText,
            imageUrl = imageUrl,
            explanation = explanation,
            hint = parsed.hint,
            presentation = parsed.presentation,
            options = options.map { it.toDomain() }
        )
    }
}

@Serializable
data class UnitQuestionsDataDto(
    val unit: QuizUnitDto,
    val questions: List<QuizQuestionDto> = emptyList()
) {
    fun toDomain() = QuizUnitSession(
        unitId = unit.id,
        title = unit.title,
        subTopic = unit.subTopic,
        xpReward = unit.xpReward,
        questions = questions.map { it.toDomain() }
    )
}

@Serializable
data class AnswerSubmissionDto(
    val questionId: String,
    val selectedOptionId: String
)

@Serializable
data class CompleteUnitRequestDto(
    val answers: List<AnswerSubmissionDto>,
    val heartsLost: Int,
    val durationSeconds: Int,
    val completedAt: String
)

@Serializable
data class CompleteUnitDataDto(
    val xpEarned: Int = 15,
    val totalXp: Int = 0,
    val currentStreak: Int = 1,
    val hearts: Int = 5,
    val unlockedNextUnitId: String? = null
) {
    fun toDomain() = UnitCompletionResult(
        xpEarned = xpEarned,
        totalXp = totalXp,
        currentStreak = currentStreak,
        hearts = hearts,
        unlockedNextUnitId = unlockedNextUnitId
    )
}
