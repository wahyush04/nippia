package id.my.kulitech.nippia.domain.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.ProfileStatsOverview
import id.my.kulitech.nippia.domain.model.WeeklyLeaderboard

interface LeaderboardRepository {
    suspend fun getWeeklyLeaderboard(): AppResult<WeeklyLeaderboard>
}

interface ProfileRepository {
    suspend fun getProfileStats(): AppResult<ProfileStatsOverview>
}
