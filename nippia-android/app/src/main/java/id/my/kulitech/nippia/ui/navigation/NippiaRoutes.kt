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
