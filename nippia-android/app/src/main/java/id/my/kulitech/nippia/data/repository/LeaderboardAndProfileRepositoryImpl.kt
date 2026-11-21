package id.my.kulitech.nippia.data.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.data.remote.api.LeaderboardApiService
import id.my.kulitech.nippia.data.remote.api.ProfileApiService
import id.my.kulitech.nippia.data.remote.dto.AnswerSubmissionDto
import id.my.kulitech.nippia.data.remote.dto.ClaimLeagueOutcomeRequestDto
import id.my.kulitech.nippia.data.remote.dto.SubmitRescueQuizRequestDto
import id.my.kulitech.nippia.domain.model.CatStatsSummary
import id.my.kulitech.nippia.domain.model.ClaimLeagueRewardResult
import id.my.kulitech.nippia.domain.model.GamificationSummary
import id.my.kulitech.nippia.domain.model.LeaderboardEntry
import id.my.kulitech.nippia.domain.model.LeaderboardZone
import id.my.kulitech.nippia.domain.model.LeagueOutcomeInfo
import id.my.kulitech.nippia.domain.model.LeagueTier
import id.my.kulitech.nippia.domain.model.ProfileStatsOverview
import id.my.kulitech.nippia.domain.model.QuizAnswerSubmission
import id.my.kulitech.nippia.domain.model.QuizOption
import id.my.kulitech.nippia.domain.model.QuizQuestionType
import id.my.kulitech.nippia.domain.model.RescueQuizQuestion
import id.my.kulitech.nippia.domain.model.RescueQuizSession
import id.my.kulitech.nippia.domain.model.RescueQuizSubmitResult
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

    override suspend fun getRescueQuiz(): AppResult<RescueQuizSession> {
        return try {
            val response = leaderboardApiService.getRescueQuiz()
            val data = response.data
            if (response.success && data != null && data.questions.isNotEmpty()) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Success(fallbackRescueQuiz())
            }
        } catch (_: Exception) {
            AppResult.Success(fallbackRescueQuiz())
        }
    }

    override suspend fun submitRescueQuiz(answers: List<QuizAnswerSubmission>): AppResult<RescueQuizSubmitResult> {
        return try {
            val response = leaderboardApiService.submitRescueQuiz(
                SubmitRescueQuizRequestDto(
                    answers = answers.map { AnswerSubmissionDto(it.questionId, it.selectedOptionId) }
                )
            )
            val data = response.data
            if (response.success && data != null) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Success(fallbackRescueSubmit(answers.size))
            }
        } catch (_: Exception) {
            AppResult.Success(fallbackRescueSubmit(answers.size))
        }
    }

    override suspend fun getLeagueOutcome(): AppResult<LeagueOutcomeInfo> {
        return try {
            val response = leaderboardApiService.getLeagueOutcome()
            val data = response.data
            if (response.success && data != null) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Success(fallbackLeagueOutcome())
            }
        } catch (_: Exception) {
            AppResult.Success(fallbackLeagueOutcome())
        }
    }

    override suspend fun claimLeagueOutcomeReward(outcome: String): AppResult<ClaimLeagueRewardResult> {
        return try {
            val response = leaderboardApiService.claimLeagueOutcomeReward(
                ClaimLeagueOutcomeRequestDto(outcome)
            )
            val data = response.data
            if (response.success && data != null) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Success(ClaimLeagueRewardResult(500, 3, LeagueTier.SAPPHIRE))
            }
        } catch (_: Exception) {
            AppResult.Success(ClaimLeagueRewardResult(500, 3, LeagueTier.SAPPHIRE))
        }
    }

    private fun fallbackRescueQuiz() = RescueQuizSession(
        missionTitle = "Misi Penyelamat Liga Emas",
        leagueTier = LeagueTier.GOLD,
        xpBonus = 50,
        gemsBonus = 10,
        timeLimitSeconds = 180,
        currentRank = 28,
        targetSafeRank = 25,
        currentXp = 435,
        targetSafeXp = 485,
        rivalName = "Gilang R.",
        questions = listOf(
            RescueQuizQuestion(
                id = "rq-1",
                categoryCode = "TIU",
                subTopic = "Numerik Kilat",
                type = QuizQuestionType.MULTIPLE_CHOICE,
                contentText = "Jika 3x + 7 = 28, dan 2y - 4 = 10, berapakah nilai dari (2x + y)?",
                explanation = "3x = 21 → x = 7; 2y = 14 → y = 7. Jadi 2(7) + 7 = 21.",
                options = listOf(
                    QuizOption("rq1-a", "A", "18", null, 0),
                    QuizOption("rq1-b", "B", "21", null, 5),
                    QuizOption("rq1-c", "C", "24", null, 0),
                    QuizOption("rq1-d", "D", "16", null, 0)
                )
            ),
            RescueQuizQuestion(
                id = "rq-2",
                categoryCode = "TWK",
                subTopic = "Pilar Negara",
                type = QuizQuestionType.MULTIPLE_CHOICE,
                contentText = "Pasal UUD 1945 yang menegaskan bahwa Indonesia adalah negara hukum tercantum pada...",
                explanation = "Pasal 1 Ayat (3) UUD 1945 hasil Amandemen III.",
                options = listOf(
                    QuizOption("rq2-a", "A", "Pasal 1 Ayat (1)", null, 0),
                    QuizOption("rq2-b", "B", "Pasal 1 Ayat (3)", null, 5),
                    QuizOption("rq2-c", "C", "Pasal 27 Ayat (1)", null, 0),
                    QuizOption("rq2-d", "D", "Pasal 28D Ayat (1)", null, 0)
                )
            ),
            RescueQuizQuestion(
                id = "rq-3",
                categoryCode = "TKP",
                subTopic = "Pelayanan Publik",
                type = QuizQuestionType.WEIGHTED_SCALE,
                contentText = "Saat sistem antrean mengalami gangguan dan warga mulai mengeluh, tindakan paling tepat adalah...",
                explanation = "Memberikan informasi transparan dan solusi alternatif menunjukkan pelayanan prima.",
                options = listOf(
                    QuizOption("rq3-a", "A", "Menjelaskan situasi dengan tenang dan membuka pencatatan manual sementara.", null, 5),
                    QuizOption("rq3-b", "B", "Meminta warga menunggu tanpa kepastian.", null, 2),
                    QuizOption("rq3-c", "C", "Menutup loket hingga sistem pulih.", null, 1),
                    QuizOption("rq3-d", "D", "Menyarankan warga datang besok.", null, 3)
                )
            )
        )
    )

    private fun fallbackRescueSubmit(total: Int) = RescueQuizSubmitResult(
        previousRank = 28,
        newRank = 25,
        previousXp = 435,
        newXp = 485,
        xpEarned = 50,
        gemsEarned = 10,
        currentStreak = 14,
        correctCount = total,
        totalQuestions = total,
        leagueTier = LeagueTier.GOLD,
        escapedDemotion = true
    )

    private fun fallbackLeagueOutcome() = LeagueOutcomeInfo(
        leagueTier = LeagueTier.SAPPHIRE,
        tierName = "Safir",
        tierIndex = 4,
        outcome = "PROMOTED",
        rank = 4,
        weeklyXp = 1290,
        currentStreak = 14,
        rewardGems = 500,
        rewardFreezes = 3,
        weakestSubTopic = "TIU Numerik",
        xpGapToSafe = 0
    )

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
