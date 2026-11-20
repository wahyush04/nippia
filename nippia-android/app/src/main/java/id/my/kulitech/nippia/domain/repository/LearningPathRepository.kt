package id.my.kulitech.nippia.domain.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.LearningPathOverview

interface LearningPathRepository {
    suspend fun getLearningPath(categoryCode: String? = null): AppResult<LearningPathOverview>
}
