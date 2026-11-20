package id.my.kulitech.nippia.ui.screens.onboarding

import app.cash.turbine.test
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.CpnsFormation
import id.my.kulitech.nippia.domain.model.DailyGoalOption
import id.my.kulitech.nippia.domain.model.DiagnosticOption
import id.my.kulitech.nippia.domain.model.DiagnosticQuestion
import id.my.kulitech.nippia.domain.model.FormationCategory
import id.my.kulitech.nippia.domain.model.OnboardingSubmissionResult
import id.my.kulitech.nippia.domain.model.PassingGradeInfo
import id.my.kulitech.nippia.domain.usecase.GetDiagnosticQuestionsUseCase
import id.my.kulitech.nippia.domain.usecase.GetFormationsUseCase
import id.my.kulitech.nippia.domain.usecase.SubmitOnboardingUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getFormationsUseCase: GetFormationsUseCase = mockk()
    private val getDiagnosticQuestionsUseCase: GetDiagnosticQuestionsUseCase = mockk()
    private val submitOnboardingUseCase: SubmitOnboardingUseCase = mockk()

    private val sampleFormations = listOf(
        CpnsFormation(
            id = "f1",
            institution = "Kementerian Hukum dan HAM",
            position = "Penjaga Tahanan",
            category = "KEMENTERIAN",
            qualification = "SLTA",
            badge = "Terpopuler #1",
            passingGrade = PassingGradeInfo(65, 80, 166, 311),
            safeTargetScore = 420,
            competitionRatio = "1 : 45"
        ),
        CpnsFormation(
            id = "f2",
            institution = "Kejaksaan Agung",
            position = "Arsiparis",
            category = "HUKUM",
            qualification = "S1",
            badge = null,
            passingGrade = PassingGradeInfo(65, 80, 166, 311),
            safeTargetScore = 405,
            competitionRatio = "1 : 36"
        )
    )

    private val sampleQuestions = listOf(
        DiagnosticQuestion(
            id = "q1",
            categoryCode = "TWK",
            topicLabel = "Pilar Negara",
            questionType = "MULTIPLE_CHOICE",
            contentText = "Soal TWK",
            explanation = "Pembahasan",
            options = listOf(
                DiagnosticOption("A", "Salah", 0),
                DiagnosticOption("B", "Benar", 5)
            )
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getFormationsUseCase() } returns AppResult.Success(sampleFormations)
        coEvery { getDiagnosticQuestionsUseCase() } returns AppResult.Success(sampleQuestions)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `filters formations by category and search query`() = runTest {
        val viewModel = OnboardingViewModel(
            getFormationsUseCase,
            getDiagnosticQuestionsUseCase,
            submitOnboardingUseCase
        )
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.filteredFormations.size)

        viewModel.onCategorySelected(FormationCategory.HUKUM)
        assertEquals(1, viewModel.uiState.value.filteredFormations.size)
        assertEquals("f2", viewModel.uiState.value.filteredFormations.first().id)

        viewModel.onCategorySelected(FormationCategory.ALL)
        viewModel.onSearchQueryChanged("Hukum")
        assertEquals(1, viewModel.uiState.value.filteredFormations.size)
        assertEquals("f1", viewModel.uiState.value.filteredFormations.first().id)
    }

    @Test
    fun `completing diagnostic quiz awards bonus and navigates on dismiss`() = runTest {
        coEvery {
            submitOnboardingUseCase(any(), any(), any(), any(), any(), true, any())
        } returns AppResult.Success(OnboardingSubmissionResult(true, 30, 10))

        val viewModel = OnboardingViewModel(
            getFormationsUseCase,
            getDiagnosticQuestionsUseCase,
            submitOnboardingUseCase
        )
        advanceUntilIdle()

        viewModel.onDailyGoalSelected(DailyGoalOption.SANTAI)
        viewModel.onOptionSelected("B")
        viewModel.onCheckAnswerOrAdvance() // checks answer
        assertTrue(viewModel.uiState.value.isAnswerChecked)
        assertEquals(1, viewModel.uiState.value.mascotCorrectTrigger)

        viewModel.onCheckAnswerOrAdvance() // submits
        advanceUntilIdle()

        assertEquals(30, viewModel.uiState.value.completionResult?.bonusXp)
        assertEquals(10, viewModel.uiState.value.completionResult?.bonusGems)

        viewModel.navEvents.test {
            viewModel.onCompletionCelebrationDismissed()
            assertEquals(OnboardingNavEvent.ToHome, awaitItem())
        }
    }

    @Test
    fun `skipping diagnostic submits with diagnosticCompleted false and navigates home`() = runTest {
        coEvery {
            submitOnboardingUseCase(any(), any(), any(), any(), any(), false, null)
        } returns AppResult.Success(OnboardingSubmissionResult(true, 0, 0))

        val viewModel = OnboardingViewModel(
            getFormationsUseCase,
            getDiagnosticQuestionsUseCase,
            submitOnboardingUseCase
        )
        advanceUntilIdle()

        viewModel.navEvents.test {
            viewModel.skipDiagnosticAndSubmit()
            advanceUntilIdle()
            assertEquals(OnboardingNavEvent.ToHome, awaitItem())
        }

        coVerify {
            submitOnboardingUseCase(
                targetInstitution = "Kementerian Hukum dan HAM",
                targetPosition = "Penjaga Tahanan",
                dailyGoal = DailyGoalOption.REGULER,
                dailyReminderEnabled = true,
                dailyReminderTime = "19:30",
                diagnosticCompleted = false,
                diagnosticScore = null
            )
        }
    }
}
