package id.my.kulitech.nippia.ui.screens.leaderboard

import app.cash.turbine.test
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.core.datastore.SessionPreferences
import id.my.kulitech.nippia.data.remote.api.ContentSyncApiService
import id.my.kulitech.nippia.domain.model.CatStatsSummary
import id.my.kulitech.nippia.domain.model.GamificationSummary
import id.my.kulitech.nippia.domain.model.LeaderboardEntry
import id.my.kulitech.nippia.domain.model.LeaderboardZone
import id.my.kulitech.nippia.domain.model.LeagueTier
import id.my.kulitech.nippia.domain.model.ProfileStatsOverview
import id.my.kulitech.nippia.domain.model.UserProfile
import id.my.kulitech.nippia.domain.model.WeeklyLeaderboard
import id.my.kulitech.nippia.domain.usecase.GetProfileStatsUseCase
import id.my.kulitech.nippia.domain.usecase.GetWeeklyLeaderboardUseCase
import id.my.kulitech.nippia.domain.usecase.LogoutUseCase
import id.my.kulitech.nippia.ui.screens.profile.ProfileNavEvent
import id.my.kulitech.nippia.ui.screens.profile.ProfileViewModel
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
class LeaderboardAndProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getWeeklyLeaderboardUseCase: GetWeeklyLeaderboardUseCase = mockk()
    private val getProfileStatsUseCase: GetProfileStatsUseCase = mockk()
    private val logoutUseCase: LogoutUseCase = mockk(relaxed = true)
    private val contentSyncApiService: ContentSyncApiService = mockk(relaxed = true)
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
    fun `LeaderboardViewModel loads league tier, topThree, and myEntry`() = runTest {
        val board = WeeklyLeaderboard(
            leagueTier = LeagueTier.GOLD,
            promotionCutoffRank = 10,
            demotionCutoffRank = 26,
            myRank = 2,
            myWeeklyXp = 1120,
            entries = listOf(
                LeaderboardEntry(1, "u1", "Rian", null, "BKN", 1780, false, LeaderboardZone.PROMOTION),
                LeaderboardEntry(2, "me", "Bayu", null, "Kemenkeu", 1120, true, LeaderboardZone.PROMOTION)
            )
        )
        coEvery { getWeeklyLeaderboardUseCase() } returns AppResult.Success(board)

        val viewModel = LeaderboardViewModel(getWeeklyLeaderboardUseCase)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(LeagueTier.GOLD, viewModel.uiState.value.leagueTier)
        assertEquals(2, viewModel.uiState.value.topThree.size)
        assertEquals("Bayu", viewModel.uiState.value.myEntry?.displayName)
    }

    @Test
    fun `ProfileViewModel loads stats and emits ToLogin on logout`() = runTest {
        val overview = ProfileStatsOverview(
            user = UserProfile("me", "b@g.com", "Bayu", null, "USER", false, "Kemenkeu", "Analis", "REGULER", 50, true, true),
            gamification = GamificationSummary(4850, 1120, 480, 14, 21, 2, 5, "GOLD"),
            catStats = CatStatsSummary(18, 398, 83.3, 85, 135, 178),
            pendingMistakesCount = 12,
            completedUnitsCount = 8
        )
        coEvery { getProfileStatsUseCase() } returns AppResult.Success(overview)

        val viewModel = ProfileViewModel(
            getProfileStatsUseCase,
            logoutUseCase,
            contentSyncApiService,
            sessionPreferences
        )
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(398, viewModel.uiState.value.overview?.catStats?.avgTotalScore)

        viewModel.navEvents.test {
            viewModel.onLogoutClick()
            advanceUntilIdle()
            assertEquals(ProfileNavEvent.ToLogin, awaitItem())
        }
        coVerify { logoutUseCase() }
    }
}
