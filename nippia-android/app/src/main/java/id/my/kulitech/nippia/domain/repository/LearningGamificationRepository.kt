package id.my.kulitech.nippia.domain.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.GamificationDetailStatus
import id.my.kulitech.nippia.domain.model.HeartRefillResult
import id.my.kulitech.nippia.domain.model.MistakeAttemptResult
import id.my.kulitech.nippia.domain.model.MistakeBankOverview
import id.my.kulitech.nippia.domain.model.StreakFreezePurchaseResult
import id.my.kulitech.nippia.domain.model.UnitStudyGuide

interface LearningGamificationRepository {
    suspend fun getUnitStudyGuide(unitId: String): AppResult<UnitStudyGuide>
    suspend fun getMistakeBank(
        categoryCode: String? = null,
        unresolvedOnly: Boolean = true
    ): AppResult<MistakeBankOverview>
    suspend fun attemptMistake(
        questionId: String,
        selectedOptionId: String
    ): AppResult<MistakeAttemptResult>
    suspend fun clearResolvedMistakes(): AppResult<Int>
    suspend fun getGamificationStatus(): AppResult<GamificationDetailStatus>
    suspend fun purchaseStreakFreeze(method: String): AppResult<StreakFreezePurchaseResult>
    suspend fun refillHearts(method: String): AppResult<HeartRefillResult>
}
