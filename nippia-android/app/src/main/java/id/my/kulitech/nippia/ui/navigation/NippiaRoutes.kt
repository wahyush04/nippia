package id.my.kulitech.nippia.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute

@Serializable
data object LoginRoute

@Serializable
data class AuthSuccessRoute(
    val displayName: String,
    val email: String,
    val avatarUrl: String?,
    val bonusXp: Int,
    val bonusGems: Int
)

@Serializable
data object OnboardingGraphRoute

@Serializable
data object OnboardingStep1Route

@Serializable
data object OnboardingStep2Route

@Serializable
data object OnboardingStep3Route

@Serializable
data object ChoosePathRoute

@Serializable
data object HomeRoute

@Serializable
data class SectionListRoute(val categoryCode: String)

@Serializable
data class QuizSessionRoute(
    val unitId: String,
    val categoryCode: String
)

@Serializable
data class TryoutGraphRoute(
    val packageId: String,
    val title: String
)

@Serializable
data object TryoutLobbyRoute

@Serializable
data object TryoutExamRoute

@Serializable
data object TryoutAnswerSheetRoute

@Serializable
data object TryoutReportRoute

@Serializable
data class TryoutReviewRoute(val attemptId: String)

@Serializable
data class StudyGuideRoute(val unitId: String)

@Serializable
data object MistakeBankRoute

@Serializable
data object SubscriptionGraphRoute

@Serializable
data object PaywallRoute

@Serializable
data object ProSuccessRoute

@Serializable
data object SubscriptionHistoryRoute

@Serializable
data object RescueQuizRoute

@Serializable
data object LeagueOutcomeRoute
