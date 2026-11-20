package id.my.kulitech.nippia.ui.screens.home

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.core.datastore.SessionPreferences
import id.my.kulitech.nippia.domain.model.GamificationSummary
import id.my.kulitech.nippia.domain.model.LearningCategoryPath
import id.my.kulitech.nippia.domain.model.LearningPathOverview
import id.my.kulitech.nippia.domain.model.LearningUnit
import id.my.kulitech.nippia.domain.model.LearningUnitStatus
import id.my.kulitech.nippia.domain.model.SkdCategoryCode
import id.my.kulitech.nippia.domain.usecase.GetLearningPathUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeLearningPathViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getLearningPathUseCase: GetLearningPathUseCase = mockk()
    private val sessionPreferences: SessionPreferences = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { sessionPreferences.selectedCategory } returns flowOf("TIU")
        every { sessionPreferences.isProUser } returns flowOf(false)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads preferred category and pending mistakes count`() = runTest {
        val overview = LearningPathOverview(
            pendingMistakesCount = 4,
            gamification = GamificationSummary(150, 80, 420, 7, 7, 1, 5, "SILVER"),
            categories = listOf(
                LearningCategoryPath("c1", SkdCategoryCode.TWK, "TWK", 65, emptyList()),
                LearningCategoryPath(
                    "c2",
                    SkdCategoryCode.TIU,
                    "TIU",
                    80,
                    listOf(
                        LearningUnit("u1", "c2", "Bagian 1", 1, "Silogisme", "Verbal", "", 15, false, LearningUnitStatus.ACTIVE, 0)
                    )
                )
            )
        )
        coEvery { getLearningPathUseCase(null) } returns AppResult.Success(overview)

        val viewModel = HomeLearningPathViewModel(getLearningPathUseCase, sessionPreferences)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(SkdCategoryCode.TIU, viewModel.uiState.value.selectedCategory)
        assertEquals(4, viewModel.uiState.value.pendingMistakesCount)
        assertEquals("TIU", viewModel.uiState.value.activeCategoryPath?.code?.code)
    }

    @Test
    fun `onSelectCategory persists category and closes sheet`() = runTest {
        coEvery { getLearningPathUseCase(null) } returns AppResult.Success(
            LearningPathOverview(0, GamificationSummary(0, 0, 0, 0, 0, 0, 5, "BRONZE"), emptyList())
        )
        val viewModel = HomeLearningPathViewModel(getLearningPathUseCase, sessionPreferences)
        advanceUntilIdle()

        viewModel.onOpenCategorySheet()
        viewModel.onSelectCategory(SkdCategoryCode.TKP)
        advanceUntilIdle()

        assertEquals(SkdCategoryCode.TKP, viewModel.uiState.value.selectedCategory)
        assertFalse(viewModel.uiState.value.showCategorySheet)
        coVerify { sessionPreferences.setSelectedCategory("TKP") }
    }
}
