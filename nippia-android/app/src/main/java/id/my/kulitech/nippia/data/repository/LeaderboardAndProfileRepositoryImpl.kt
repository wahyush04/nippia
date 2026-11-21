package id.my.kulitech.nippia.data.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.data.remote.api.LeaderboardApiService
import id.my.kulitech.nippia.data.remote.api.ProfileApiService
import id.my.kulitech.nippia.domain.model.CatStatsSummary
import id.my.kulitech.nippia.domain.model.GamificationSummary
import id.my.kulitech.nippia.domain.model.LeaderboardEntry
import id.my.kulitech.nippia.domain.model.LeaderboardZone
import id.my.kulitech.nippia.domain.model.LeagueTier
import id.my.kulitech.nippia.domain.model.ProfileStatsOverview
import id.my.kulitech.nippia.domain.model.UserProfile
import id.my.kulitech.nippia.domain.model.WeeklyLeaderboard
import id.my.kulitech.nippia.domain.repository.LeaderboardRepository
import id.my.kulitech.nippia.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepositoryImpl @Inject constructor(
    private val leaderboardApiService: LeaderboardApiService
) : LeaderboardRepository {

    override suspend fun getWeeklyLeaderboard(): AppResult<WeeklyLeaderboard> {
        return try {
            val response = leaderboardApiService.getWeeklyLeaderboard()
            val data = response.data
            if (response.success && data != null && data.entries.isNotEmpty()) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Success(fallbackLeaderboard())
            }
        } catch (_: Exception) {
            AppResult.Success(fallbackLeaderboard())
        }
    }

    private fun fallbackLeaderboard(): WeeklyLeaderboard = WeeklyLeaderboard(
        leagueTier = LeagueTier.GOLD,
        promotionCutoffRank = 10,
        demotionCutoffRank = 26,
        myRank = 4,
        myWeeklyXp = 1120,
        entries = listOf(
            LeaderboardEntry(1, "u1", "Rian Pratama", null, "BKN Pusat", 1780, false, LeaderboardZone.PROMOTION),
            LeaderboardEntry(2, "u2", "Bambang P.", null, "Setneg RI", 1420, false, LeaderboardZone.PROMOTION),
            LeaderboardEntry(3, "u3", "Siti Nurhaliza", null, "Kemenkeu RI", 1350, false, LeaderboardZone.PROMOTION),
            LeaderboardEntry(4, "me", "Anda (Pejuang NIP)", null, "Kemenkeu - Analis Kebijakan", 1120, true, LeaderboardZone.PROMOTION),
            LeaderboardEntry(5, "u5", "Dewi Anggraini", null, "Kejaksaan Agung RI", 1040, false, LeaderboardZone.PROMOTION),
            LeaderboardEntry(11, "u11", "Gilang Ramadhan", null, "Pemprov DKI Jakarta", 970, false, LeaderboardZone.SAFE),
            LeaderboardEntry(26, "u26", "Rizky Febrian", null, "Kemendes PDTT", 420, false, LeaderboardZone.DEMOTION)
        )
    )
}

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileApiService: ProfileApiService
) : ProfileRepository {

    override suspend fun getProfileStats(): AppResult<ProfileStatsOverview> {
        return try {
            val response = profileApiService.getProfileStats()
            val data = response.data
            if (response.success && data != null) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Success(fallbackProfileStats())
            }
        } catch (_: Exception) {
            AppResult.Success(fallbackProfileStats())
        }
    }

    private fun fallbackProfileStats(): ProfileStatsOverview = ProfileStatsOverview(
        user = UserProfile(
            id = "me",
            email = "pejuang.nip@gmail.com",
            displayName = "Pejuang NIP",
            avatarUrl = null,
            role = "USER",
            isGuest = false,
            targetInstitution = "Kemenkeu RI",
            targetPosition = "Analis Kebijakan",
            dailyGoal = "REGULER",
            dailyXpTarget = 50,
            onboardingCompleted = true,
            isPro = false
        ),
        gamification = GamificationSummary(
            totalXp = 4850,
            weeklyXp = 1120,
            gems = 480,
            currentStreak = 14,
            longestStreak = 21,
            streakFreezes = 2,
            hearts = 5,
            leagueTier = "GOLD"
        ),
        catStats = CatStatsSummary(
            totalAttempts = 18,
            avgTotalScore = 398,
            passRatePercent = 83.3,
            avgTwkScore = 85,
            avgTiuScore = 135,
            avgTkpScore = 178
        ),
        pendingMistakesCount = 12,
        completedUnitsCount = 8
    )
}
