package id.my.kulitech.nippia.ui.screens.section

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.core.datastore.SessionPreferences
import id.my.kulitech.nippia.domain.model.GamificationSummary
import id.my.kulitech.nippia.domain.model.LearningCategoryPath
import id.my.kulitech.nippia.domain.model.LearningPathOverview
import id.my.kulitech.nippia.domain.model.LearningSectionStatus
import id.my.kulitech.nippia.domain.model.LearningUnit
import id.my.kulitech.nippia.domain.model.LearningUnitStatus
import id.my.kulitech.nippia.domain.model.SkdCategoryCode
import id.my.kulitech.nippia.domain.usecase.GetLearningPathUseCase
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
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SectionListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getLearningPathUseCase: GetLearningPathUseCase = mockk()
    private val sessionPreferences: SessionPreferences = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `groups units into sections with accurate status and counts`() = runTest {
        val units = listOf(
            LearningUnit("u1", "c1", "Bagian 1 • Pilar Negara", 1, "Pancasila", "Nasionalisme", "Panduan 1", 15, false, LearningUnitStatus.COMPLETED, 100),
            LearningUnit("u2", "c1", "Bagian 1 • Pilar Negara", 2, "UUD 1945", "Konstitusi", "Panduan 2", 15, false, LearningUnitStatus.ACTIVE, 0),
            LearningUnit("u3", "c1", "Bagian 2 • Bela Negara", 3, "NKRI", "Bela Negara", "Panduan 3", 15, false, LearningUnitStatus.LOCKED, 0)
        )
        val overview = LearningPathOverview(
            pendingMistakesCount = 0,
            gamification = GamificationSummary(50, 50, 10, 1, 1, 0, 5, "BRONZE"),
            categories = listOf(
                LearningCategoryPath("c1", SkdCategoryCode.TWK, "Tes Wawasan Kebangsaan", 65, units)
            )
        )
        coEvery { getLearningPathUseCase("TWK") } returns AppResult.Success(overview)

        val viewModel = SectionListViewModel(getLearningPathUseCase, sessionPreferences)
        viewModel.loadCategory("TWK")
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(2, viewModel.uiState.value.sections.size)
        assertEquals(LearningSectionStatus.ACTIVE, viewModel.uiState.value.sections[0].status)
        assertEquals(1, viewModel.uiState.value.sections[0].completedCount)
        assertEquals(2, viewModel.uiState.value.sections[0].totalCount)
        assertEquals(LearningSectionStatus.LOCKED, viewModel.uiState.value.sections[1].status)
        assertEquals(2, viewModel.uiState.value.remainingUnits)
        coVerify { sessionPreferences.setSelectedCategory("TWK") }
    }
}
