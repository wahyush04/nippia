package id.my.kulitech.nippia.domain.model

data class UserProfile(
    val id: String,
    val email: String?,
    val displayName: String?,
    val avatarUrl: String?,
    val role: String,
    val isGuest: Boolean,
    val targetInstitution: String?,
    val targetPosition: String?,
    val dailyGoal: String,
    val dailyXpTarget: Int,
    val onboardingCompleted: Boolean,
    val isPro: Boolean
)

data class GamificationSummary(
    val totalXp: Int,
    val weeklyXp: Int,
    val gems: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val streakFreezes: Int,
    val hearts: Int,
    val leagueTier: String
)

data class WelcomeBonus(
    val xp: Int,
    val gems: Int
)

data class AuthSession(
    val isNewUser: Boolean,
    val welcomeBonus: WelcomeBonus?,
    val user: UserProfile,
    val gamification: GamificationSummary
)

sealed interface SessionState {
    data object Unauthenticated : SessionState
    data class Authenticated(
        val user: UserProfile,
        val gamification: GamificationSummary?
    ) : SessionState
}
