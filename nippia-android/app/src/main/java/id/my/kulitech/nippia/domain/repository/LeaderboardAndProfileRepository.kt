package id.my.kulitech.nippia.domain.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.ClaimLeagueRewardResult
import id.my.kulitech.nippia.domain.model.LeagueOutcomeInfo
import id.my.kulitech.nippia.domain.model.ProfileStatsOverview
import id.my.kulitech.nippia.domain.model.QuizAnswerSubmission
import id.my.kulitech.nippia.domain.model.RescueQuizSession
import id.my.kulitech.nippia.domain.model.RescueQuizSubmitResult
import id.my.kulitech.nippia.domain.model.WeeklyLeaderboard

interface LeaderboardRepository {
    suspend fun getWeeklyLeaderboard(): AppResult<WeeklyLeaderboard>
    suspend fun getRescueQuiz(): AppResult<RescueQuizSession>
    suspend fun submitRescueQuiz(answers: List<QuizAnswerSubmission>): AppResult<RescueQuizSubmitResult>
    suspend fun getLeagueOutcome(): AppResult<LeagueOutcomeInfo>
    suspend fun claimLeagueOutcomeReward(outcome: String): AppResult<ClaimLeagueRewardResult>
}

interface ProfileRepository {
    suspend fun getProfileStats(): AppResult<ProfileStatsOverview>
}
