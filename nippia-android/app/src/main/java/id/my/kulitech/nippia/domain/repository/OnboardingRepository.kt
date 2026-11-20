package id.my.kulitech.nippia.domain.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.CpnsFormation
import id.my.kulitech.nippia.domain.model.DailyGoalOption
import id.my.kulitech.nippia.domain.model.DiagnosticQuestion
import id.my.kulitech.nippia.domain.model.DiagnosticScore
import id.my.kulitech.nippia.domain.model.OnboardingSubmissionResult

interface OnboardingRepository {
    suspend fun getFormations(): AppResult<List<CpnsFormation>>
    suspend fun getDiagnosticQuestions(): AppResult<List<DiagnosticQuestion>>
    suspend fun submitOnboarding(
        targetInstitution: String,
        targetPosition: String,
        dailyGoal: DailyGoalOption,
        dailyReminderEnabled: Boolean,
        dailyReminderTime: String,
        diagnosticCompleted: Boolean,
        diagnosticScore: DiagnosticScore?
    ): AppResult<OnboardingSubmissionResult>
}
