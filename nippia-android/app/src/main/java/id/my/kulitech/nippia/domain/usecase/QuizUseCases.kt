package id.my.kulitech.nippia.domain.usecase

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.QuizAnswerSubmission
import id.my.kulitech.nippia.domain.model.QuizUnitSession
import id.my.kulitech.nippia.domain.model.UnitCompletionResult
import id.my.kulitech.nippia.domain.repository.QuizRepository
import javax.inject.Inject

class GetUnitQuestionsUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(unitId: String, fallbackCategory: String = "TWK"): AppResult<QuizUnitSession> =
        quizRepository.getUnitQuestions(unitId = unitId, fallbackCategory = fallbackCategory)
}

class CompleteUnitUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(
        unitId: String,
        answers: List<QuizAnswerSubmission>,
        heartsLost: Int,
        durationSeconds: Int
    ): AppResult<UnitCompletionResult> = quizRepository.completeUnit(
        unitId = unitId,
        answers = answers,
        heartsLost = heartsLost,
        durationSeconds = durationSeconds
    )
}
