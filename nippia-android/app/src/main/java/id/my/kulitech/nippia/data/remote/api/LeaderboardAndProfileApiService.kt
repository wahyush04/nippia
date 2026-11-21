package id.my.kulitech.nippia.data.remote.api

import id.my.kulitech.nippia.data.remote.dto.ApiResponse
import id.my.kulitech.nippia.data.remote.dto.ProfileStatsDataDto
import id.my.kulitech.nippia.data.remote.dto.WeeklyLeaderboardDataDto
import retrofit2.http.GET

interface LeaderboardApiService {
    @GET("leaderboard/weekly")
    suspend fun getWeeklyLeaderboard(): ApiResponse<WeeklyLeaderboardDataDto>
}

interface ProfileApiService {
    @GET("users/me/stats")
    suspend fun getProfileStats(): ApiResponse<ProfileStatsDataDto>
}
