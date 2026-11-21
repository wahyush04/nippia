package id.my.kulitech.nippia.data.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.data.remote.api.LearningPathApiService
import id.my.kulitech.nippia.domain.model.GamificationSummary
import id.my.kulitech.nippia.domain.model.LearningCategoryPath
import id.my.kulitech.nippia.domain.model.LearningPathOverview
import id.my.kulitech.nippia.domain.model.LearningUnit
import id.my.kulitech.nippia.domain.model.LearningUnitStatus
import id.my.kulitech.nippia.domain.model.SkdCategoryCode
import id.my.kulitech.nippia.domain.repository.LearningPathRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LearningPathRepositoryImpl @Inject constructor(
    private val learningPathApiService: LearningPathApiService
) : LearningPathRepository {

    override suspend fun getLearningPath(categoryCode: String?): AppResult<LearningPathOverview> {
        return try {
            val response = learningPathApiService.getLearningPath(categoryCode)
            val data = response.data
            if (response.success && data != null && data.categories.isNotEmpty()) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Success(fallbackOverview())
            }
        } catch (_: Exception) {
            AppResult.Success(fallbackOverview())
        }
    }

    private fun fallbackOverview(): LearningPathOverview = LearningPathOverview(
        pendingMistakesCount = 3,
        gamification = GamificationSummary(
            totalXp = 50,
            weeklyXp = 50,
            gems = 10,
            currentStreak = 1,
            longestStreak = 1,
            streakFreezes = 0,
            hearts = 5,
            leagueTier = "BRONZE"
        ),
        categories = listOf(
            LearningCategoryPath(
                id = "cat-twk",
                code = SkdCategoryCode.TWK,
                name = "Tes Wawasan Kebangsaan",
                passingGrade = 65,
                units = listOf(
                    sampleUnit("twk-1", "cat-twk", "Bagian 1 • Pilar Negara & Nasionalisme", 1, "Pancasila I", "Nasionalisme", LearningUnitStatus.COMPLETED, 100),
                    sampleUnit("twk-2", "cat-twk", "Bagian 1 • Pilar Negara & Nasionalisme", 2, "UUD 1945", "Pilar Negara", LearningUnitStatus.ACTIVE, 0),
                    sampleUnit("twk-3", "cat-twk", "Bagian 1 • Pilar Negara & Nasionalisme", 3, "Bhinneka Tunggal Ika", "Integritas", LearningUnitStatus.LOCKED, 0),
                    sampleUnit("twk-4", "cat-twk", "Bagian 2 • Konstitusi & Bela Negara", 4, "NKRI & Wawasan", "Bela Negara", LearningUnitStatus.LOCKED, 0),
                    sampleUnit("twk-5", "cat-twk", "Bagian 2 • Konstitusi & Bela Negara", 5, "Bahasa Negara & EYD", "Bahasa Negara", LearningUnitStatus.LOCKED, 0),
                    sampleUnit("twk-6", "cat-twk", "Bagian 3 • Evaluasi Pilar Kebangsaan", 6, "Ujian Unit TWK", "Evaluasi", LearningUnitStatus.LOCKED, 0)
                )
            ),
            LearningCategoryPath(
                id = "cat-tiu",
                code = SkdCategoryCode.TIU,
                name = "Tes Intelegensia Umum",
                passingGrade = 80,
                units = listOf(
                    sampleUnit("tiu-1", "cat-tiu", "Bagian 1 • Verbal & Silogisme", 1, "Analogi Kata", "Verbal", LearningUnitStatus.ACTIVE, 0),
                    sampleUnit("tiu-2", "cat-tiu", "Bagian 1 • Verbal & Silogisme", 2, "Silogisme Dasar", "Logika", LearningUnitStatus.LOCKED, 0),
                    sampleUnit("tiu-3", "cat-tiu", "Bagian 2 • Numerik & Deret", 3, "Deret Angka", "Numerik", LearningUnitStatus.LOCKED, 0),
                    sampleUnit("tiu-4", "cat-tiu", "Bagian 3 • Figural 2D", 4, "Figural 2D", "Figural", LearningUnitStatus.LOCKED, 0)
                )
            ),
            LearningCategoryPath(
                id = "cat-tkp",
                code = SkdCategoryCode.TKP,
                name = "Tes Karakteristik Pribadi",
                passingGrade = 166,
                units = listOf(
                    sampleUnit("tkp-1", "cat-tkp", "Bagian 1 • Pelayanan & Jejaring", 1, "Pelayanan Publik", "Pelayanan Publik", LearningUnitStatus.ACTIVE, 0),
                    sampleUnit("tkp-2", "cat-tkp", "Bagian 1 • Pelayanan & Jejaring", 2, "Jejaring Kerja", "Jejaring Kerja", LearningUnitStatus.LOCKED, 0),
                    sampleUnit("tkp-3", "cat-tkp", "Bagian 2 • Sosial Budaya & TIK", 3, "Sosial Budaya", "Sosial Budaya", LearningUnitStatus.LOCKED, 0),
                    sampleUnit("tkp-4", "cat-tkp", "Bagian 2 • Sosial Budaya & TIK", 4, "Profesionalisme", "Profesionalisme", LearningUnitStatus.LOCKED, 0)
                )
            )
        )
    )

    private fun sampleUnit(
        id: String,
        categoryId: String,
        sectionTitle: String,
        orderIndex: Int,
        title: String,
        subTopic: String,
        status: LearningUnitStatus,
        bestScore: Int
    ) = LearningUnit(
        id = id,
        categoryId = categoryId,
        sectionTitle = sectionTitle,
        orderIndex = orderIndex,
        title = title,
        subTopic = subTopic,
        guideMarkdown = "Rangkuman materi **$title** ($subTopic): fokus pada konsep inti, kata kunci soal BKN, dan trik eliminasi cepat.",
        xpReward = 15,
        isProOnly = false,
        status = status,
        bestScore = bestScore
    )
}
