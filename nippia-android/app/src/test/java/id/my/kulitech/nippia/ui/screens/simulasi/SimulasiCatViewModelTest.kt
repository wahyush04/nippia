package id.my.kulitech.nippia.ui.screens.simulasi

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.ScheduledTryoutEvent
import id.my.kulitech.nippia.domain.model.TryoutCatalog
import id.my.kulitech.nippia.domain.model.TryoutPackageItem
import id.my.kulitech.nippia.domain.model.TryoutType
import id.my.kulitech.nippia.domain.usecase.GetTryoutCatalogUseCase
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SimulasiCatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getTryoutCatalogUseCase: GetTryoutCatalogUseCase = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads catalog and switches between Live Akbar and Mandiri tabs`() = runTest {
        val catalog = TryoutCatalog(
            packages = listOf(
                TryoutPackageItem("p1", "Full CAT", "Desc", TryoutType.FULL_CAT, 100, 110, false, null)
            ),
            scheduledEvent = ScheduledTryoutEvent("e1", "Tryout Akbar #12", "Sabtu", 1280, 100, 110)
        )
        coEvery { getTryoutCatalogUseCase() } returns AppResult.Success(catalog)

        val viewModel = SimulasiCatViewModel(getTryoutCatalogUseCase)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(1, viewModel.uiState.value.packages.size)
        assertEquals("Tryout Akbar #12", viewModel.uiState.value.scheduledEvent?.title)

        viewModel.onModeSelected(SimulasiModeTab.MANDIRI)
        assertEquals(SimulasiModeTab.MANDIRI, viewModel.uiState.value.selectedMode)

        viewModel.onOpenPassingGradeModal()
        assertTrue(viewModel.uiState.value.showPassingGradeModal)
        viewModel.onDismissPassingGradeModal()
        assertFalse(viewModel.uiState.value.showPassingGradeModal)
    }
}
