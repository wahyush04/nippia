package id.my.kulitech.nippia.ui.screens.quiz

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.QuestionPresentation
import id.my.kulitech.nippia.domain.model.QuestionPresentationParser
import id.my.kulitech.nippia.domain.model.QuizOption
import id.my.kulitech.nippia.domain.model.QuizQuestion
import id.my.kulitech.nippia.domain.model.QuizQuestionType
import id.my.kulitech.nippia.domain.model.QuizUnitSession
import id.my.kulitech.nippia.domain.model.UnitCompletionResult
import id.my.kulitech.nippia.domain.usecase.CompleteUnitUseCase
import id.my.kulitech.nippia.domain.usecase.GetUnitQuestionsUseCase
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
class QuizModelsAndViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getUnitQuestionsUseCase: GetUnitQuestionsUseCase = mockk()
    private val completeUnitUseCase: CompleteUnitUseCase = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `QuestionPresentationParser detects PremiseLogic, AnalyticalSlots, and StandardText`() {
        val premiseParsed = QuestionPresentationParser.parse(
            """
            [KONTEKS: Kisi TIU]
            [PETUNJUK: Gunakan hukum silogisme]
            P1: Semua ASN berintegritas.
            P2: Sebagian peserta adalah ASN.
            Kesimpulan yang tepat adalah...
            """.trimIndent()
        )
        assertTrue(premiseParsed.presentation is QuestionPresentation.PremiseLogic)
        val pLogic = premiseParsed.presentation as QuestionPresentation.PremiseLogic
        assertEquals(2, pLogic.premises.size)
        assertEquals("Kisi TIU", pLogic.contextBadge)
        assertEquals("Gunakan hukum silogisme", premiseParsed.hint)

        val diagramParsed = QuestionPresentationParser.parse(
            """
            [DENAH: Denah CAT | Meja 1=Andi | Meja 2=?]
            Siapakah di Meja 2?
            """.trimIndent()
        )
        assertTrue(diagramParsed.presentation is QuestionPresentation.AnalyticalSlots)
        val slots = (diagramParsed.presentation as QuestionPresentation.AnalyticalSlots).slots
        assertEquals(2, slots.size)
        assertTrue(slots[1].isUnknown)

        val standardParsed = QuestionPresentationParser.parse(
            "Narasi kasus panjang.\nApa tindakan yang tepat?"
        )
        assertTrue(standardParsed.presentation is QuestionPresentation.StandardText)
        assertEquals("Narasi kasus panjang.", (standardParsed.presentation as QuestionPresentation.StandardText).narrative)
    }

    @Test
    fun `QuizViewModel deducts heart on wrong MULTIPLE_CHOICE but not on WEIGHTED_SCALE`() = runTest {
        val q1 = QuizQuestion(
            id = "q1",
            categoryCode = "TWK",
            subTopic = "Bela Negara",
            type = QuizQuestionType.MULTIPLE_CHOICE,
            orderIndex = 1,
            contentText = "Soal 1?",
            imageUrl = null,
            explanation = "Pembahasan 1",
            hint = null,
            presentation = QuestionPresentation.StandardText(null, null, "Soal 1?"),
            options = listOf(
                QuizOption("o1", "A", "Salah", null, 0),
                QuizOption("o2", "B", "Benar", null, 5)
            )
        )
        val q2 = QuizQuestion(
            id = "q2",
            categoryCode = "TKP",
            subTopic = "Pelayanan",
            type = QuizQuestionType.WEIGHTED_SCALE,
            orderIndex = 2,
            contentText = "Soal 2?",
            imageUrl = null,
            explanation = "Pembahasan 2",
            hint = null,
            presentation = QuestionPresentation.StandardText(null, null, "Soal 2?"),
            options = listOf(
                QuizOption("o3", "A", "Skor 2", null, 2),
                QuizOption("o4", "B", "Skor 5", null, 5)
            )
        )
        coEvery { getUnitQuestionsUseCase("u1", "TWK") } returns AppResult.Success(
            QuizUnitSession("u1", "Unit 1", "Bela Negara", 15, listOf(q1, q2))
        )
        coEvery { completeUnitUseCase("u1", any(), 1, any()) } returns AppResult.Success(
            UnitCompletionResult(15, 65, 2, 4, "u2")
        )

        val viewModel = QuizViewModel(getUnitQuestionsUseCase, completeUnitUseCase)
        viewModel.loadUnit("u1", "TWK")
        advanceUntilIdle()

        // Answer q1 wrongly
        viewModel.onOptionSelected("o1")
        viewModel.onCheckAnswer()
        assertEquals(4, viewModel.uiState.value.hearts)
        assertEquals(1, viewModel.uiState.value.heartsLost)
        assertEquals(1, viewModel.uiState.value.mascotWrongTrigger)
        assertEquals(1, viewModel.uiState.value.heartCrackTrigger)

        viewModel.onContinueAfterFeedback()

        // Answer q2 with score 2 (TKP never deducts heart)
        viewModel.onOptionSelected("o3")
        viewModel.onCheckAnswer()
        assertEquals(4, viewModel.uiState.value.hearts)
        assertEquals(1, viewModel.uiState.value.heartsLost)

        viewModel.onContinueAfterFeedback()
        advanceUntilIdle()

        assertEquals(15, viewModel.uiState.value.completionResult?.xpEarned)
        coVerify { completeUnitUseCase("u1", any(), 1, any()) }
    }
}
