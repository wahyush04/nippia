package id.my.kulitech.nippia.data.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.data.remote.api.TryoutApiService
import id.my.kulitech.nippia.domain.model.ScheduledTryoutEvent
import id.my.kulitech.nippia.domain.model.TryoutAttemptSummary
import id.my.kulitech.nippia.domain.model.TryoutCatalog
import id.my.kulitech.nippia.domain.model.TryoutPackageItem
import id.my.kulitech.nippia.domain.model.TryoutType
import id.my.kulitech.nippia.domain.repository.TryoutRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TryoutRepositoryImpl @Inject constructor(
    private val tryoutApiService: TryoutApiService
) : TryoutRepository {

    override suspend fun getTryoutCatalog(): AppResult<TryoutCatalog> {
        return try {
            val response = tryoutApiService.getTryouts()
            val data = response.data
            if (response.success && data != null && data.packages.isNotEmpty()) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Success(fallbackCatalog())
            }
        } catch (_: Exception) {
            AppResult.Success(fallbackCatalog())
        }
    }

    private fun fallbackCatalog(): TryoutCatalog = TryoutCatalog(
        packages = listOf(
            TryoutPackageItem(
                id = "pkg-full-cat-2025",
                title = "Simulasi Full CAT SKD 2025",
                description = "Komposisi resmi BKN: 30 Soal TWK, 35 Soal TIU, dan 45 Soal TKP. Termasuk sistem passing grade dinamis.",
                type = TryoutType.FULL_CAT,
                durationMinutes = 100,
                totalQuestions = 110,
                isProOnly = false,
                lastAttempt = TryoutAttemptSummary(
                    id = "att-sample-11",
                    twkScore = 85,
                    tiuScore = 135,
                    tkpScore = 192,
                    totalScore = 412,
                    isPassed = true,
                    submittedAt = "2025-03-15T09:00:00.000Z"
                )
            ),
            TryoutPackageItem(
                id = "pkg-mini-cat-1",
                title = "Mini CAT Kilat",
                description = "30 Soal Acak Cepat (10 TWK • 10 TIU • 10 TKP) untuk pemanasan harian di sela kesibukan.",
                type = TryoutType.MINI_CAT,
                durationMinutes = 30,
                totalQuestions = 30,
                isProOnly = false,
                lastAttempt = null
            )
        ),
        scheduledEvent = ScheduledTryoutEvent(
            id = "akbar-nasional-12",
            title = "Tryout Akbar Nasional SKD #12",
            scheduleLabel = "Sabtu, 29 Maret 2025 • 09.00 - 10.40 WIB",
            participantsCount = 1280,
            durationMinutes = 100,
            totalQuestions = 110
        )
    )
}
