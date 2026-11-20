package id.my.kulitech.nippia.domain.usecase

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.LearningPathOverview
import id.my.kulitech.nippia.domain.repository.LearningPathRepository
import javax.inject.Inject

class GetLearningPathUseCase @Inject constructor(
    private val learningPathRepository: LearningPathRepository
) {
    suspend operator fun invoke(categoryCode: String? = null): AppResult<LearningPathOverview> =
        learningPathRepository.getLearningPath(categoryCode = categoryCode)
}
