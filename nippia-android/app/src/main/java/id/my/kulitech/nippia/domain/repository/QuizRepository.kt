package id.my.kulitech.nippia.domain.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.QuizAnswerSubmission
import id.my.kulitech.nippia.domain.model.QuizUnitSession
import id.my.kulitech.nippia.domain.model.UnitCompletionResult

interface QuizRepository {
    suspend fun getUnitQuestions(unitId: String, fallbackCategory: String = "TWK"): AppResult<QuizUnitSession>
    suspend fun completeUnit(
        unitId: String,
        answers: List<QuizAnswerSubmission>,
        heartsLost: Int,
        durationSeconds: Int
    ): AppResult<UnitCompletionResult>
}
