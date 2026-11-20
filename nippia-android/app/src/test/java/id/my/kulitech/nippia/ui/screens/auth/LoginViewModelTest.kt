package id.my.kulitech.nippia.ui.screens.auth

import app.cash.turbine.test
import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.AuthSession
import id.my.kulitech.nippia.domain.model.GamificationSummary
import id.my.kulitech.nippia.domain.model.UserProfile
import id.my.kulitech.nippia.domain.model.WelcomeBonus
import id.my.kulitech.nippia.domain.usecase.LoginAsGuestUseCase
import id.my.kulitech.nippia.domain.usecase.LoginWithGoogleUseCase
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase = mockk()
    private val loginAsGuestUseCase: LoginAsGuestUseCase = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onGoogleIdTokenReceived navigates to AuthSuccess for new user with welcomeBonus`() = runTest {
        val session = fakeSession(isNewUser = true, welcomeBonus = WelcomeBonus(50, 10), onboardingCompleted = false)
        coEvery { loginWithGoogleUseCase("token-123", null) } returns AppResult.Success(session)

        val viewModel = LoginViewModel(loginWithGoogleUseCase, loginAsGuestUseCase)

        viewModel.navEvents.test {
            viewModel.onGoogleIdTokenReceived("token-123")
            advanceUntilIdle()
            val event = awaitItem()
            assertEquals(LoginNavEvent.ToAuthSuccess(session), event)
            assertNull(viewModel.uiState.value.errorMessage)
        }
    }

    @Test
    fun `onGuestLoginClick navigates to Onboarding when onboarding not completed`() = runTest {
        val session = fakeSession(isNewUser = true, welcomeBonus = null, onboardingCompleted = false, isGuest = true)
        coEvery { loginAsGuestUseCase(null) } returns AppResult.Success(session)

        val viewModel = LoginViewModel(loginWithGoogleUseCase, loginAsGuestUseCase)

        viewModel.navEvents.test {
            viewModel.onGuestLoginClick()
            advanceUntilIdle()
            assertEquals(LoginNavEvent.ToOnboarding, awaitItem())
        }
    }

    @Test
    fun `onGoogleIdTokenReceived sets errorMessage on failure`() = runTest {
        coEvery { loginWithGoogleUseCase("bad-token", null) } returns AppResult.Error("Token tidak valid")

        val viewModel = LoginViewModel(loginWithGoogleUseCase, loginAsGuestUseCase)
        viewModel.onGoogleIdTokenReceived("bad-token")
        advanceUntilIdle()

        assertEquals("Token tidak valid", viewModel.uiState.value.errorMessage)
    }

    private fun fakeSession(
        isNewUser: Boolean,
        welcomeBonus: WelcomeBonus?,
        onboardingCompleted: Boolean,
        isGuest: Boolean = false
    ) = AuthSession(
        isNewUser = isNewUser,
        welcomeBonus = welcomeBonus,
        user = UserProfile(
            id = "u1",
            email = if (isGuest) null else "rian@gmail.com",
            displayName = "Rian Aditya",
            avatarUrl = null,
            role = "USER",
            isGuest = isGuest,
            targetInstitution = null,
            targetPosition = null,
            dailyGoal = "REGULER",
            dailyXpTarget = 50,
            onboardingCompleted = onboardingCompleted,
            isPro = false
        ),
        gamification = GamificationSummary(
            totalXp = welcomeBonus?.xp ?: 0,
            weeklyXp = welcomeBonus?.xp ?: 0,
            gems = welcomeBonus?.gems ?: 0,
            currentStreak = 0,
            longestStreak = 0,
            streakFreezes = 0,
            hearts = 5,
            leagueTier = "BRONZE"
        )
    )
}
