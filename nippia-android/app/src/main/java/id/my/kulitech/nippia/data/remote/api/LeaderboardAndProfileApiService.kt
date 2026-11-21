package id.my.kulitech.nippia.data.remote.api

import id.my.kulitech.nippia.data.remote.dto.ApiResponse
import id.my.kulitech.nippia.data.remote.dto.ClaimLeagueOutcomeRequestDto
import id.my.kulitech.nippia.data.remote.dto.ClaimLeagueRewardDataDto
import id.my.kulitech.nippia.data.remote.dto.LeagueOutcomeDataDto
import id.my.kulitech.nippia.data.remote.dto.ProfileStatsDataDto
import id.my.kulitech.nippia.data.remote.dto.RescueQuizDataDto
import id.my.kulitech.nippia.data.remote.dto.RescueQuizSubmitDataDto
import id.my.kulitech.nippia.data.remote.dto.SubmitRescueQuizRequestDto
import id.my.kulitech.nippia.data.remote.dto.WeeklyLeaderboardDataDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LeaderboardApiService {
    @GET("leaderboard/weekly")
    suspend fun getWeeklyLeaderboard(): ApiResponse<WeeklyLeaderboardDataDto>

    @GET("leaderboard/rescue-quiz")
    suspend fun getRescueQuiz(): ApiResponse<RescueQuizDataDto>

    @POST("leaderboard/rescue-quiz/submit")
    suspend fun submitRescueQuiz(
        @Body body: SubmitRescueQuizRequestDto
    ): ApiResponse<RescueQuizSubmitDataDto>

    @GET("leaderboard/league-outcome")
    suspend fun getLeagueOutcome(): ApiResponse<LeagueOutcomeDataDto>

    @POST("leaderboard/league-outcome/claim")
    suspend fun claimLeagueOutcomeReward(
        @Body body: ClaimLeagueOutcomeRequestDto
    ): ApiResponse<ClaimLeagueRewardDataDto>
}

interface ProfileApiService {
    @GET("users/me/stats")
    suspend fun getProfileStats(): ApiResponse<ProfileStatsDataDto>
}
