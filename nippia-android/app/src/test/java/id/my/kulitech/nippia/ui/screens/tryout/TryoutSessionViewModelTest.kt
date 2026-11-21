package id.my.kulitech.nippia.ui.screens.tryout

import app.cash.turbine.test
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.QuestionPresentation
import id.my.kulitech.nippia.domain.model.QuizOption
import id.my.kulitech.nippia.domain.model.QuizQuestionType
import id.my.kulitech.nippia.domain.model.SubtestGradeResult
import id.my.kulitech.nippia.domain.model.TryoutLobbyInfo
import id.my.kulitech.nippia.domain.model.TryoutQuestion
import id.my.kulitech.nippia.domain.model.TryoutSession
import id.my.kulitech.nippia.domain.model.TryoutSubmitResult
import id.my.kulitech.nippia.domain.model.TryoutType
import id.my.kulitech.nippia.domain.usecase.GetTryoutLobbyUseCase
import id.my.kulitech.nippia.domain.usecase.StartCategoryDrillUseCase
import id.my.kulitech.nippia.domain.usecase.StartTryoutUseCase
import id.my.kulitech.nippia.domain.usecase.SubmitTryoutUseCase
import io.mockk.coEvery
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
class TryoutSessionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getTryoutLobbyUseCase: GetTryoutLobbyUseCase = mockk()
    private val startTryoutUseCase: StartTryoutUseCase = mockk()
    private val startCategoryDrillUseCase: StartCategoryDrillUseCase = mockk()
    private val submitTryoutUseCase: SubmitTryoutUseCase = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `starts exam, records answers and flags, and submits to report`() = runTest {
        val session = TryoutSession(
            attemptId = "att-1",
            packageId = "pkg-1",
            title = "Tryout Akbar #12",
            type = TryoutType.FULL_CAT,
            durationMinutes = 100,
            startedAt = "2026-11-21T00:00:00Z",
            expiresAt = "2026-11-21T01:40:00Z",
            questions = listOf(
                TryoutQuestion(
                    id = "q1",
                    orderIndex = 1,
                    categoryCode = "TWK",
                    subTopic = "Nasionalisme",
                    type = QuizQuestionType.MULTIPLE_CHOICE,
                    contentText = "Soal 1",
                    imageUrl = null,
                    presentation = QuestionPresentation.StandardText(null, null, "Soal 1"),
                    hint = null,
                    options = listOf(QuizOption("o1", "A", "Opsi A", null, 0))
                )
            )
        )
        val submitResult = TryoutSubmitResult(
            attemptId = "att-1",
            packageId = "pkg-1",
            twkScore = 95,
            tiuScore = 135,
            tkpScore = 182,
            totalScore = 412,
            twkGrade = SubtestGradeResult(65, true),
            tiuGrade = SubtestGradeResult(80, true),
            tkpGrade = SubtestGradeResult(166, true),
            isPassed = true,
            subTopicBreakdown = emptyList()
        )
        coEvery { getTryoutLobbyUseCase("pkg-1", "Tryout Akbar #12") } returns AppResult.Success(
            TryoutLobbyInfo("pkg-1", "Tryout Akbar #12", 100, 110, "NIP-1234", 1280)
        )
        coEvery { startTryoutUseCase("pkg-1", "Tryout Akbar #12") } returns AppResult.Success(session)
        coEvery { submitTryoutUseCase("att-1", any()) } returns AppResult.Success(submitResult)

        val viewModel = TryoutSessionViewModel(
            getTryoutLobbyUseCase,
            startTryoutUseCase,
            startCategoryDrillUseCase,
            submitTryoutUseCase
        )
        viewModel.initLobby("pkg-1", "Tryout Akbar #12")

        viewModel.navEvents.test {
            viewModel.startExam()
            assertEquals(TryoutNavEvent.ToExam, awaitItem())

            viewModel.onSelectOption("q1", "o1")
            viewModel.onToggleFlag("q1")
            assertEquals(1, viewModel.uiState.value.answeredCount)
            assertEquals(1, viewModel.uiState.value.flaggedCount)

            viewModel.submitExam()
            assertEquals(TryoutNavEvent.ToReport, awaitItem())
            assertTrue(viewModel.uiState.value.submitResult?.isPassed == true)
            assertEquals(412, viewModel.uiState.value.submitResult?.totalScore)
        }
    }
}
