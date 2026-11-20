package id.my.kulitech.nippia.data.remote.dto

import id.my.kulitech.nippia.domain.model.AuthSession
import id.my.kulitech.nippia.domain.model.GamificationSummary
import id.my.kulitech.nippia.domain.model.UserProfile
import id.my.kulitech.nippia.domain.model.WelcomeBonus
import kotlinx.serialization.Serializable

@Serializable
data class GoogleAuthRequest(
    val idToken: String,
    val fcmToken: String? = null
)

@Serializable
data class GuestAuthRequest(
    val fcmToken: String? = null
)

@Serializable
data class LinkGoogleRequest(
    val idToken: String
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class WelcomeBonusDto(
    val xp: Int,
    val gems: Int
) {
    fun toDomain() = WelcomeBonus(xp = xp, gems = gems)
}

@Serializable
data class UserDto(
    val id: String,
    val email: String? = null,
    val googleId: String? = null,
    val displayName: String? = null,
    val avatarUrl: String? = null,
    val role: String,
    val isGuest: Boolean,
    val targetInstitution: String? = null,
    val targetPosition: String? = null,
    val dailyGoal: String,
    val dailyXpTarget: Int,
    val dailyReminderEnabled: Boolean = true,
    val dailyReminderTime: String = "19:30",
    val onboardingCompleted: Boolean = false,
    val isPro: Boolean = false,
    val createdAt: String
) {
    fun toDomain() = UserProfile(
        id = id,
        email = email,
        displayName = displayName,
        avatarUrl = avatarUrl,
        role = role,
        isGuest = isGuest,
        targetInstitution = targetInstitution,
        targetPosition = targetPosition,
        dailyGoal = dailyGoal,
        dailyXpTarget = dailyXpTarget,
        onboardingCompleted = onboardingCompleted,
        isPro = isPro
    )
}

@Serializable
data class GamificationDto(
    val totalXp: Int,
    val weeklyXp: Int,
    val gems: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val streakFreezes: Int,
    val hearts: Int,
    val lastHeartRegenAt: String? = null,
    val lastActiveDate: String? = null,
    val leagueTier: String
) {
    fun toDomain() = GamificationSummary(
        totalXp = totalXp,
        weeklyXp = weeklyXp,
        gems = gems,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        streakFreezes = streakFreezes,
        hearts = hearts,
        leagueTier = leagueTier
    )
}

@Serializable
data class AuthDataDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int,
    val isNewUser: Boolean,
    val welcomeBonus: WelcomeBonusDto? = null,
    val user: UserDto,
    val gamification: GamificationDto
) {
    fun toDomain() = AuthSession(
        isNewUser = isNewUser,
        welcomeBonus = welcomeBonus?.toDomain(),
        user = user.toDomain(),
        gamification = gamification.toDomain()
    )
}

@Serializable
data class RefreshDataDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int
)

@Serializable
data class UserProfileDataDto(
    val user: UserDto,
    val gamification: GamificationDto? = null
)
