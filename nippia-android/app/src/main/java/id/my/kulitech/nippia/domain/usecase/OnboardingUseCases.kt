package id.my.kulitech.nippia.domain.usecase

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.CpnsFormation
import id.my.kulitech.nippia.domain.model.DailyGoalOption
import id.my.kulitech.nippia.domain.model.DiagnosticQuestion
import id.my.kulitech.nippia.domain.model.DiagnosticScore
import id.my.kulitech.nippia.domain.model.OnboardingSubmissionResult
import id.my.kulitech.nippia.domain.repository.OnboardingRepository
import javax.inject.Inject

class GetFormationsUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    suspend operator fun invoke(): AppResult<List<CpnsFormation>> =
        onboardingRepository.getFormations()
}

class GetDiagnosticQuestionsUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    suspend operator fun invoke(): AppResult<List<DiagnosticQuestion>> =
        onboardingRepository.getDiagnosticQuestions()
}

class SubmitOnboardingUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    suspend operator fun invoke(
        targetInstitution: String,
        targetPosition: String,
        dailyGoal: DailyGoalOption,
        dailyReminderEnabled: Boolean,
        dailyReminderTime: String,
        diagnosticCompleted: Boolean,
        diagnosticScore: DiagnosticScore?
    ): AppResult<OnboardingSubmissionResult> = onboardingRepository.submitOnboarding(
        targetInstitution = targetInstitution,
        targetPosition = targetPosition,
        dailyGoal = dailyGoal,
        dailyReminderEnabled = dailyReminderEnabled,
        dailyReminderTime = dailyReminderTime,
        diagnosticCompleted = diagnosticCompleted,
        diagnosticScore = diagnosticScore
    )
}
