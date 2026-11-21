package id.my.kulitech.nippia.domain.usecase

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.QuizAnswerSubmission
import id.my.kulitech.nippia.domain.model.TryoutAttemptReview
import id.my.kulitech.nippia.domain.model.TryoutCatalog
import id.my.kulitech.nippia.domain.model.TryoutLobbyInfo
import id.my.kulitech.nippia.domain.model.TryoutSession
import id.my.kulitech.nippia.domain.model.TryoutSubmitResult
import id.my.kulitech.nippia.domain.repository.TryoutRepository
import javax.inject.Inject

class GetTryoutCatalogUseCase @Inject constructor(
    private val tryoutRepository: TryoutRepository
) {
    suspend operator fun invoke(): AppResult<TryoutCatalog> =
        tryoutRepository.getTryoutCatalog()
}

class GetTryoutLobbyUseCase @Inject constructor(
    private val tryoutRepository: TryoutRepository
) {
    suspend operator fun invoke(packageId: String, fallbackTitle: String): AppResult<TryoutLobbyInfo> =
        tryoutRepository.getTryoutLobby(packageId = packageId, fallbackTitle = fallbackTitle)
}

class StartTryoutUseCase @Inject constructor(
    private val tryoutRepository: TryoutRepository
) {
    suspend operator fun invoke(packageId: String, title: String): AppResult<TryoutSession> =
        tryoutRepository.startTryout(packageId = packageId, title = title)
}

class StartCategoryDrillUseCase @Inject constructor(
    private val tryoutRepository: TryoutRepository
) {
    suspend operator fun invoke(categoryCode: String): AppResult<TryoutSession> =
        tryoutRepository.startCategoryDrill(categoryCode = categoryCode)
}

class SubmitTryoutUseCase @Inject constructor(
    private val tryoutRepository: TryoutRepository
) {
    suspend operator fun invoke(
        attemptId: String,
        answers: List<QuizAnswerSubmission>
    ): AppResult<TryoutSubmitResult> =
        tryoutRepository.submitTryout(attemptId = attemptId, answers = answers)
}

class GetAttemptReviewUseCase @Inject constructor(
    private val tryoutRepository: TryoutRepository
) {
    suspend operator fun invoke(attemptId: String): AppResult<TryoutAttemptReview> =
        tryoutRepository.getAttemptReview(attemptId = attemptId)
}
