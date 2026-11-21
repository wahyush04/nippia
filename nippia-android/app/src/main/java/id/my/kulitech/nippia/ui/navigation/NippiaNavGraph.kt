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
import id.my.kulitech.nippia.ui.screens.guide.StudyGuideScreen
import id.my.kulitech.nippia.ui.screens.home.HomeLearningPathScreen
import id.my.kulitech.nippia.ui.screens.mistakes.MistakeBankScreen
import id.my.kulitech.nippia.ui.screens.onboarding.OnboardingStep1Screen
import id.my.kulitech.nippia.ui.screens.onboarding.OnboardingStep2Screen
import id.my.kulitech.nippia.ui.screens.onboarding.OnboardingStep3Screen
import id.my.kulitech.nippia.ui.screens.onboarding.OnboardingViewModel
import id.my.kulitech.nippia.ui.screens.path.ChooseLearningPathScreen
import id.my.kulitech.nippia.ui.screens.quiz.QuizSessionScreen
import id.my.kulitech.nippia.ui.screens.section.SectionListScreen
import id.my.kulitech.nippia.ui.screens.splash.SplashScreen
import id.my.kulitech.nippia.ui.screens.subscription.PaywallScreen
import id.my.kulitech.nippia.ui.screens.subscription.ProSuccessScreen
import id.my.kulitech.nippia.ui.screens.subscription.SubscriptionHistoryScreen
import id.my.kulitech.nippia.ui.screens.subscription.SubscriptionViewModel
import id.my.kulitech.nippia.ui.screens.tryout.TryoutAnswerSheetScreen
import id.my.kulitech.nippia.ui.screens.tryout.TryoutExamScreen
import id.my.kulitech.nippia.ui.screens.tryout.TryoutLobbyScreen
import id.my.kulitech.nippia.ui.screens.tryout.TryoutReportScreen
import id.my.kulitech.nippia.ui.screens.tryout.TryoutReviewScreen
import id.my.kulitech.nippia.ui.screens.tryout.TryoutSessionViewModel

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
                onOpenStudyGuide = { unitId ->
                    navController.navigate(StudyGuideRoute(unitId))
                },
                onOpenMistakeBank = {
                    navController.navigate(MistakeBankRoute)
                },
                onOpenPaywall = {
                    navController.navigate(SubscriptionGraphRoute)
                },
                onOpenSubscriptionHistory = {
                    navController.navigate(SubscriptionHistoryRoute)
                },
                onOpenTryout = { packageId, title ->
                    navController.navigate(TryoutGraphRoute(packageId, title))
                },
                onOpenTryoutReview = { attemptId ->
                    navController.navigate(TryoutReviewRoute(attemptId))
                },
                onLoggedOut = {
                    navController.navigate(LoginRoute) {
                        popUpTo<HomeRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<StudyGuideRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<StudyGuideRoute>()
            StudyGuideScreen(
                unitId = args.unitId,
                onBackClick = { navController.popBackStack() },
                onStartPractice = { unitId, categoryCode ->
                    navController.navigate(QuizSessionRoute(unitId, categoryCode)) {
                        popUpTo<StudyGuideRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<MistakeBankRoute> {
            MistakeBankScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        navigation<SubscriptionGraphRoute>(
            startDestination = PaywallRoute
        ) {
            composable<PaywallRoute> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<SubscriptionGraphRoute>()
                }
                val viewModel: SubscriptionViewModel = hiltViewModel(parentEntry)
                PaywallScreen(
                    viewModel = viewModel,
                    onCloseClick = { navController.popBackStack() },
                    onPurchaseSuccess = {
                        navController.navigate(ProSuccessRoute) {
                            popUpTo<PaywallRoute> { inclusive = true }
                        }
                    }
                )
            }

            composable<ProSuccessRoute> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<SubscriptionGraphRoute>()
                }
                val viewModel: SubscriptionViewModel = hiltViewModel(parentEntry)
                ProSuccessScreen(
                    viewModel = viewModel,
                    onStartUnlimited = {
                        navController.popBackStack(SubscriptionGraphRoute, inclusive = true)
                    },
                    onDownloadOffline = {
                        navController.popBackStack(SubscriptionGraphRoute, inclusive = true)
                    }
                )
            }
        }

        composable<SubscriptionHistoryRoute> {
            val viewModel: SubscriptionViewModel = hiltViewModel()
            SubscriptionHistoryScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onOpenPaywall = {
                    navController.navigate(SubscriptionGraphRoute)
                }
            )
        }

        composable<QuizSessionRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<QuizSessionRoute>()
            QuizSessionScreen(
                unitId = args.unitId,
                categoryCode = args.categoryCode,
                onCloseClick = { navController.popBackStack() },
                onCompleted = { navController.popBackStack() },
                onOpenMistakeBank = {
                    navController.navigate(MistakeBankRoute) {
                        popUpTo<QuizSessionRoute> { inclusive = true }
                    }
                }
            )
        }

        navigation<TryoutGraphRoute>(
            startDestination = TryoutLobbyRoute
        ) {
            composable<TryoutLobbyRoute> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<TryoutGraphRoute>()
                }
                val args = parentEntry.toRoute<TryoutGraphRoute>()
                val viewModel: TryoutSessionViewModel = hiltViewModel(parentEntry)
                TryoutLobbyScreen(
                    packageId = args.packageId,
                    title = args.title,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToExam = {
                        navController.navigate(TryoutExamRoute) {
                            popUpTo<TryoutLobbyRoute> { inclusive = true }
                        }
                    }
                )
            }

            composable<TryoutExamRoute> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<TryoutGraphRoute>()
                }
                val viewModel: TryoutSessionViewModel = hiltViewModel(parentEntry)
                TryoutExamScreen(
                    viewModel = viewModel,
                    onExitExam = {
                        navController.popBackStack<TryoutGraphRoute>(inclusive = true)
                    },
                    onOpenAnswerSheet = {
                        navController.navigate(TryoutAnswerSheetRoute)
                    },
                    onNavigateToReport = {
                        navController.navigate(TryoutReportRoute) {
                            popUpTo<TryoutExamRoute> { inclusive = true }
                        }
                    }
                )
            }

            composable<TryoutAnswerSheetRoute> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<TryoutGraphRoute>()
                }
                val viewModel: TryoutSessionViewModel = hiltViewModel(parentEntry)
                TryoutAnswerSheetScreen(
                    viewModel = viewModel,
                    onBackToExam = { navController.popBackStack() },
                    onJumpToQuestion = { index ->
                        viewModel.onJumpToQuestion(index)
                        navController.popBackStack()
                    }
                )
            }

            composable<TryoutReportRoute> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<TryoutGraphRoute>()
                }
                val viewModel: TryoutSessionViewModel = hiltViewModel(parentEntry)
                TryoutReportScreen(
                    viewModel = viewModel,
                    onOpenReview = { attemptId ->
                        navController.navigate(TryoutReviewRoute(attemptId))
                    },
                    onBackToSimulasi = {
                        navController.popBackStack<TryoutGraphRoute>(inclusive = true)
                    }
                )
            }
        }

        composable<TryoutReviewRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<TryoutReviewRoute>()
            TryoutReviewScreen(
                attemptId = args.attemptId,
                onBackClick = { navController.popBackStack() }
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
