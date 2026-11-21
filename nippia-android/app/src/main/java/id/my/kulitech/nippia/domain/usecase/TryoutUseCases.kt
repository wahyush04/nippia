package id.my.kulitech.nippia.domain.usecase

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.QuizAnswerSubmission
import id.my.kulitech.nippia.domain.model.TryoutCatalog
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

class StartTryoutUseCase @Inject constructor(
    private val tryoutRepository: TryoutRepository
) {
    suspend operator fun invoke(packageId: String, title: String): AppResult<TryoutSession> =
        tryoutRepository.startTryout(packageId = packageId, title = title)
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
