package id.my.kulitech.nippia.core.network.auth

import kotlinx.serialization.Serializable

@Serializable
data class GoogleLoginRequest(
    val idToken: String,
    val fcmToken: String? = null,
)

@Serializable
data class GuestLoginRequest(
    val fcmToken: String? = null,
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String,
)

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int,
    val isNewUser: Boolean,
    val user: UserDto,
    val gamification: GamificationDto,
)

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
    val dailyReminderEnabled: Boolean,
    val dailyReminderTime: String,
    val onboardingCompleted: Boolean,
    val isPro: Boolean,
    val createdAt: String,
)

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
    val leagueTier: String,
)

@Serializable
data class RefreshResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int,
)
