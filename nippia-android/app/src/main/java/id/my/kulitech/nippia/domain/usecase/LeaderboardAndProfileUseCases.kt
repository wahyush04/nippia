package id.my.kulitech.nippia.domain.usecase

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.ProfileStatsOverview
import id.my.kulitech.nippia.domain.model.WeeklyLeaderboard
import id.my.kulitech.nippia.domain.repository.AuthRepository
import id.my.kulitech.nippia.domain.repository.LeaderboardRepository
import id.my.kulitech.nippia.domain.repository.ProfileRepository
import javax.inject.Inject

class GetWeeklyLeaderboardUseCase @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository
) {
    suspend operator fun invoke(): AppResult<WeeklyLeaderboard> =
        leaderboardRepository.getWeeklyLeaderboard()
}

class GetProfileStatsUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(): AppResult<ProfileStatsOverview> =
        profileRepository.getProfileStats()
}

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.logout()
}
