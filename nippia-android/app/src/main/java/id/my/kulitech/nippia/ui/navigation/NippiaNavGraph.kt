package id.my.kulitech.nippia.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import id.my.kulitech.nippia.ui.screens.auth.AuthSuccessScreen
import id.my.kulitech.nippia.ui.screens.auth.LoginScreen
import id.my.kulitech.nippia.ui.screens.home.HomeLearningPathScreen
import id.my.kulitech.nippia.ui.screens.onboarding.OnboardingStep1Screen
import id.my.kulitech.nippia.ui.screens.onboarding.OnboardingStep2Screen
import id.my.kulitech.nippia.ui.screens.onboarding.OnboardingStep3Screen
import id.my.kulitech.nippia.ui.screens.onboarding.OnboardingViewModel
import id.my.kulitech.nippia.ui.screens.path.ChooseLearningPathScreen
import id.my.kulitech.nippia.ui.screens.quiz.QuizSessionScreen
import id.my.kulitech.nippia.ui.screens.section.SectionListScreen
import id.my.kulitech.nippia.ui.screens.splash.SplashScreen

@Composable
fun NippiaNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = SplashRoute
    ) {
        composable<SplashRoute> {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(LoginRoute) {
                        popUpTo<SplashRoute> { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(OnboardingGraphRoute) {
                        popUpTo<SplashRoute> { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(HomeRoute) {
                        popUpTo<SplashRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<LoginRoute> {
            LoginScreen(
                onNavigateToAuthSuccess = { session ->
                    val route = AuthSuccessRoute(
                        displayName = session.user.displayName ?: "Pejuang NIP",
                        email = session.user.email ?: "",
                        avatarUrl = session.user.avatarUrl,
                        bonusXp = session.welcomeBonus?.xp ?: 50,
                        bonusGems = session.welcomeBonus?.gems ?: 10
                    )
                    navController.navigate(route) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(OnboardingGraphRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(HomeRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<AuthSuccessRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<AuthSuccessRoute>()
            AuthSuccessScreen(
                displayName = args.displayName,
                email = args.email,
                avatarUrl = args.avatarUrl,
                bonusXp = args.bonusXp,
                bonusGems = args.bonusGems,
                onContinueOnboarding = {
                    navController.navigate(OnboardingGraphRoute) {
                        popUpTo<AuthSuccessRoute> { inclusive = true }
                    }
                },
                onExploreHome = {
                    navController.navigate(HomeRoute) {
                        popUpTo<AuthSuccessRoute> { inclusive = true }
                    }
                }
            )
        }

        navigation<OnboardingGraphRoute>(
            startDestination = OnboardingStep1Route
        ) {
            composable<OnboardingStep1Route> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<OnboardingGraphRoute>()
                }
                val viewModel: OnboardingViewModel = hiltViewModel(parentEntry)
                OnboardingStep1Screen(
                    viewModel = viewModel,
                    onBackClick = {
                        navController.navigate(HomeRoute) {
                            popUpTo<OnboardingGraphRoute> { inclusive = true }
                        }
                    },
                    onContinueClick = {
                        navController.navigate(OnboardingStep2Route)
                    }
                )
            }

            composable<OnboardingStep2Route> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<OnboardingGraphRoute>()
                }
                val viewModel: OnboardingViewModel = hiltViewModel(parentEntry)
                OnboardingStep2Screen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onContinueClick = {
                        navController.navigate(OnboardingStep3Route)
                    }
                )
            }

            composable<OnboardingStep3Route> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<OnboardingGraphRoute>()
                }
                val viewModel: OnboardingViewModel = hiltViewModel(parentEntry)
                OnboardingStep3Screen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate(ChoosePathRoute) {
                            popUpTo<OnboardingGraphRoute> { inclusive = true }
                        }
                    }
                )
            }
        }

        composable<ChoosePathRoute> {
            ChooseLearningPathScreen(
                onBackClick = {
                    navController.navigate(HomeRoute) {
                        popUpTo<ChoosePathRoute> { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(HomeRoute) {
                        popUpTo<ChoosePathRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<HomeRoute> {
            HomeLearningPathScreen(
                onOpenSectionList = { category ->
                    navController.navigate(SectionListRoute(category.code))
                },
                onOpenUnitQuiz = { unitId, categoryCode ->
                    navController.navigate(QuizSessionRoute(unitId, categoryCode))
                },
                onLoggedOut = {
                    navController.navigate(LoginRoute) {
                        popUpTo<HomeRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<QuizSessionRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<QuizSessionRoute>()
            QuizSessionScreen(
                unitId = args.unitId,
                categoryCode = args.categoryCode,
                onCloseClick = { navController.popBackStack() },
                onCompleted = { navController.popBackStack() }
            )
        }

        composable<SectionListRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<SectionListRoute>()
            SectionListScreen(
                categoryCode = args.categoryCode,
                onBackClick = { navController.popBackStack() },
                onContinueSection = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun DestinationPlaceholder(
    title: String,
    subtitle: String
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
