package id.my.kulitech.nippia.data.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.data.remote.api.LearningGamificationApiService
import id.my.kulitech.nippia.data.remote.dto.GamificationMethodRequestDto
import id.my.kulitech.nippia.data.remote.dto.MistakeAttemptRequestDto
import id.my.kulitech.nippia.domain.model.GamificationDetailStatus
import id.my.kulitech.nippia.domain.model.HeartRefillResult
import id.my.kulitech.nippia.domain.model.MistakeAttemptResult
import id.my.kulitech.nippia.domain.model.MistakeBankItem
import id.my.kulitech.nippia.domain.model.MistakeBankOverview
import id.my.kulitech.nippia.domain.model.QuizOption
import id.my.kulitech.nippia.domain.model.QuizQuestionType
import id.my.kulitech.nippia.domain.model.StreakFreezePurchaseResult
import id.my.kulitech.nippia.domain.model.StreakWeekDay
import id.my.kulitech.nippia.domain.model.UnitStudyGuide
import id.my.kulitech.nippia.domain.repository.LearningGamificationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LearningGamificationRepositoryImpl @Inject constructor(
    private val apiService: LearningGamificationApiService
) : LearningGamificationRepository {

    override suspend fun getUnitStudyGuide(unitId: String): AppResult<UnitStudyGuide> {
        return try {
            val response = apiService.getUnitStudyGuide(unitId)
            val data = response.data
            if (response.success && data != null) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Success(fallbackStudyGuide(unitId))
            }
        } catch (_: Exception) {
            AppResult.Success(fallbackStudyGuide(unitId))
        }
    }

    override suspend fun getMistakeBank(
        categoryCode: String?,
        unresolvedOnly: Boolean
    ): AppResult<MistakeBankOverview> {
        return try {
            val response = apiService.getMistakeBank(categoryCode, unresolvedOnly)
            val data = response.data
            if (response.success && data != null) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Success(fallbackMistakeBank(categoryCode))
            }
        } catch (_: Exception) {
            AppResult.Success(fallbackMistakeBank(categoryCode))
        }
    }

    override suspend fun attemptMistake(
        questionId: String,
        selectedOptionId: String
    ): AppResult<MistakeAttemptResult> {
        return try {
            val response = apiService.attemptMistake(
                questionId = questionId,
                body = MistakeAttemptRequestDto(selectedOptionId)
            )
            val data = response.data
            if (response.success && data != null) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Error(response.error?.message ?: "Gagal mengirim jawaban.")
            }
        } catch (_: Exception) {
            val isCorrect = selectedOptionId.endsWith("b") || selectedOptionId.endsWith("a")
            AppResult.Success(
                MistakeAttemptResult(
                    questionId = questionId,
                    isCorrect = isCorrect,
                    earnedScore = if (isCorrect) 5 else 0,
                    isResolved = isCorrect,
                    xpEarned = if (isCorrect) 5 else 0,
                    explanation = "Perbandingan berbalik nilai: 40 × 24 = 30 × pekerja → 32 pekerja (tambahan 8 pekerja)."
                )
            )
        }
    }

    override suspend fun clearResolvedMistakes(): AppResult<Int> {
        return try {
            val response = apiService.clearResolvedMistakes()
            AppResult.Success(response.data?.deletedCount ?: 0)
        } catch (_: Exception) {
            AppResult.Success(0)
        }
    }

    override suspend fun getGamificationStatus(): AppResult<GamificationDetailStatus> {
        return try {
            val response = apiService.getGamificationStatus()
            val data = response.data
            if (response.success && data != null) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Success(fallbackGamification())
            }
        } catch (_: Exception) {
            AppResult.Success(fallbackGamification())
        }
    }

    override suspend fun purchaseStreakFreeze(method: String): AppResult<StreakFreezePurchaseResult> {
        return try {
            val response = apiService.purchaseStreakFreeze(GamificationMethodRequestDto(method))
            val data = response.data
            if (response.success && data != null) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Error(response.error?.message ?: "Gagal mengaktifkan Pelindung Streak.")
            }
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Gagal mengaktifkan Pelindung Streak.")
        }
    }

    override suspend fun refillHearts(method: String): AppResult<HeartRefillResult> {
        return try {
            val response = apiService.refillHearts(GamificationMethodRequestDto(method))
            val data = response.data
            if (response.success && data != null) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Error(response.error?.message ?: "Gagal mengisi ulang nyawa.")
            }
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Gagal mengisi ulang nyawa.")
        }
    }

    private fun fallbackStudyGuide(unitId: String) = UnitStudyGuide(
        unitId = unitId,
        title = "UUD 1945 & Amandemen",
        sectionTitle = "Pilar Negara",
        categoryCode = "TWK",
        categoryName = "Tes Wawasan Kebangsaan",
        subTopic = "Konstitusi & Pilar Negara",
        xpReward = 25,
        questionCount = 7,
        readMinutes = 3,
        guideMarkdown = """
            # Kronologi 4 Kali Amandemen (1999 - 2002)
            Pahami materi konstitusi pokok, poin perubahan 4 kali sidang umum, dan pasal krusial langganan soal CAT BKN.

            ## Amandemen I (1999) — Sidang Umum MPR
            Fokus Utama: Pembatasan kekuasaan eksekutif dan pergeseran kekuasaan legislasi utama ke DPR.
            Poin Inti: Masa jabatan Presiden maksimal 2 periode (5 thn × 2). Pasal: 5, 7, 9, 13, 14, 15, 17, 20, 21.

            ## Amandemen II (2000) — Sidang Tahunan MPR
            Fokus Utama: Desentralisasi pemda, penegakan Hak Asasi Manusia, dan pertahanan negara.
            Poin Inti: Penambahan 10 pasal HAM (28A–28J), Otonomi Daerah, dan Sistem Sishankamrata.

            ## Amandemen III (2001) — Sidang Tahunan MPR
            Fokus Utama: Rekonstruksi kelembagaan negara dan mekanisme demokrasi langsung.
            Poin Inti: Pembentukan MK, DPD, KY, Pemilu Langsung Presiden/Wapres, dan BPK mandiri.

            ## Amandemen IV (2002) — Sidang Tahunan MPR
            Fokus Utama: Penyempurnaan struktur lembaga, pendidikan nasional, dan perekonomian.
            Poin Inti: Penghapusan DPA, alokasi anggaran pendidikan min. 20%, Bank Sentral independen.

            ## Pasal Langganan Soal BKN
            - Pasal 1: Negara Kesatuan berbentuk Republik; Indonesia adalah Negara Hukum.
            - Pasal 27: Equality before the law & hak/kewajiban upaya Bela Negara.
            - Pasal 30: TNI & POLRI sebagai Kekuatan Utama, Rakyat sebagai Kekuatan Pendukung (Sishankamrata).
            - Pasal 31: Setiap warga negara berhak mendapat pendidikan; minimal 20% APBN & APBD.
        """.trimIndent()
    )

    private fun fallbackMistakeBank(categoryCode: String?): MistakeBankOverview {
        val items = listOf(
            MistakeBankItem(
                id = "m-1",
                questionId = "q-mistake-1",
                categoryCode = "TIU",
                subTopic = "Perbandingan Berbalik Nilai",
                type = QuizQuestionType.MULTIPLE_CHOICE,
                contentText = "Sebuah proyek pembangunan jembatan direncanakan selesai 40 hari oleh 24 pekerja. Jika proyek harus selesai dalam 30 hari, tambahan pekerja yang dibutuhkan adalah...",
                imageUrl = null,
                explanation = "Perbandingan berbalik nilai: 40 × 24 = 30 × P → P = 32 pekerja. Tambahan = 32 - 24 = 8 pekerja.",
                wrongCount = 3,
                isResolved = false,
                updatedAt = "2 jam lalu",
                options = listOf(
                    QuizOption("m1-a", "A", "6 pekerja", null, 0),
                    QuizOption("m1-b", "B", "8 pekerja", null, 5),
                    QuizOption("m1-c", "C", "10 pekerja", null, 0),
                    QuizOption("m1-d", "D", "12 pekerja", null, 0)
                )
            ),
            MistakeBankItem(
                id = "m-2",
                questionId = "q-mistake-2",
                categoryCode = "TWK",
                subTopic = "Pasal 27 Ayat 3 & Doktrin Hankamrata",
                type = QuizQuestionType.MULTIPLE_CHOICE,
                contentText = "Sikap warga negara yang rela berkorban menjaga kedaulatan bangsa dalam konteks digital merupakan implementasi dari...",
                imageUrl = null,
                explanation = "Literasi digital kritis merupakan wujud bela negara modern sesuai Pasal 27 Ayat 3 UUD 1945.",
                wrongCount = 2,
                isResolved = false,
                updatedAt = "Kemarin",
                options = listOf(
                    QuizOption("m2-a", "A", "Pasal 27 Ayat 3 & Bela Negara", null, 5),
                    QuizOption("m2-b", "B", "Pasal 28J tentang HAM", null, 0),
                    QuizOption("m2-c", "C", "Pasal 33 tentang Perekonomian", null, 0),
                    QuizOption("m2-d", "D", "Pasal 31 tentang Pendidikan", null, 0)
                )
            )
        ).filter { categoryCode == null || it.categoryCode == categoryCode }

        return MistakeBankOverview(
            unresolvedCount = items.size,
            resolvedCount = 5,
            remedialRatePercent = 71,
            dailyTarget = 5,
            items = items
        )
    }

    private fun fallbackGamification() = GamificationDetailStatus(
        totalXp = 1240,
        weeklyXp = 320,
        gems = 480,
        currentStreak = 12,
        longestStreak = 14,
        streakFreezes = 1,
        maxStreakFreezes = 2,
        hearts = 5,
        maxHearts = 5,
        nextHeartRegenSeconds = 0,
        weekDays = listOf(
            StreakWeekDay("Sen", active = true, isToday = false),
            StreakWeekDay("Sel", active = true, isToday = false),
            StreakWeekDay("Rab", active = true, isToday = false),
            StreakWeekDay("Kam", active = true, isToday = false),
            StreakWeekDay("Jum", active = true, isToday = false),
            StreakWeekDay("Sab", active = true, isToday = true),
            StreakWeekDay("Min", active = false, isToday = false)
        )
    )
}
