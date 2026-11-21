package id.my.kulitech.nippia.domain.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.QuizAnswerSubmission
import id.my.kulitech.nippia.domain.model.TryoutAttemptReview
import id.my.kulitech.nippia.domain.model.TryoutCatalog
import id.my.kulitech.nippia.domain.model.TryoutLobbyInfo
import id.my.kulitech.nippia.domain.model.TryoutSession
import id.my.kulitech.nippia.domain.model.TryoutSubmitResult

interface TryoutRepository {
    suspend fun getTryoutCatalog(): AppResult<TryoutCatalog>
    suspend fun getTryoutLobby(packageId: String, fallbackTitle: String): AppResult<TryoutLobbyInfo>
    suspend fun startTryout(packageId: String, title: String): AppResult<TryoutSession>
    suspend fun startCategoryDrill(categoryCode: String): AppResult<TryoutSession>
    suspend fun submitTryout(
        attemptId: String,
        answers: List<QuizAnswerSubmission>
    ): AppResult<TryoutSubmitResult>
    suspend fun getAttemptReview(attemptId: String): AppResult<TryoutAttemptReview>
}
