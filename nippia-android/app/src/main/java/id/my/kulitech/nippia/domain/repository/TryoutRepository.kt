package id.my.kulitech.nippia.domain.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.QuizAnswerSubmission
import id.my.kulitech.nippia.domain.model.TryoutCatalog
import id.my.kulitech.nippia.domain.model.TryoutSession
import id.my.kulitech.nippia.domain.model.TryoutSubmitResult

interface TryoutRepository {
    suspend fun getTryoutCatalog(): AppResult<TryoutCatalog>
    suspend fun startTryout(packageId: String, title: String): AppResult<TryoutSession>
    suspend fun submitTryout(
        attemptId: String,
        answers: List<QuizAnswerSubmission>
    ): AppResult<TryoutSubmitResult>
}
