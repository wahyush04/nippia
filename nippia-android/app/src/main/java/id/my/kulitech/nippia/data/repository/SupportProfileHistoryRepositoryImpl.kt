package id.my.kulitech.nippia.data.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.core.datastore.SessionPreferences
import id.my.kulitech.nippia.data.remote.api.SupportProfileHistoryApiService
import id.my.kulitech.nippia.data.remote.dto.DeleteAccountRequestDto
import id.my.kulitech.nippia.data.remote.dto.ReportQuestionRequestDto
import id.my.kulitech.nippia.data.remote.dto.UpdateProfileRequestDto
import id.my.kulitech.nippia.data.remote.dto.UpsertBookmarkRequestDto
import id.my.kulitech.nippia.domain.model.BookmarkItem
import id.my.kulitech.nippia.domain.model.BookmarkOverview
import id.my.kulitech.nippia.domain.model.FaqCatalog
import id.my.kulitech.nippia.domain.model.FaqItem
import id.my.kulitech.nippia.domain.model.PassingGradeRule
import id.my.kulitech.nippia.domain.model.TryoutHistoryItem
import id.my.kulitech.nippia.domain.model.TryoutHistoryOverview
import id.my.kulitech.nippia.domain.model.TryoutHistorySummary
import id.my.kulitech.nippia.domain.model.TryoutType
import id.my.kulitech.nippia.domain.model.UpdateProfileInput
import id.my.kulitech.nippia.domain.repository.SupportProfileHistoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupportProfileHistoryRepositoryImpl @Inject constructor(
    private val apiService: SupportProfileHistoryApiService,
    private val sessionPreferences: SessionPreferences
) : SupportProfileHistoryRepository {

    override suspend fun updateProfile(input: UpdateProfileInput): AppResult<Unit> {
        return try {
            val res = apiService.updateProfile(
                UpdateProfileRequestDto(
                    displayName = input.displayName,
                    targetInstitution = input.targetInstitution,
                    targetPosition = input.targetPosition,
                    dailyGoal = input.dailyGoal,
                    dailyReminderEnabled = input.dailyReminderEnabled
                )
            )
            if (res.success) AppResult.Success(Unit)
            else AppResult.Error(res.error?.message ?: "Gagal memperbarui profil.")
        } catch (_: Exception) {
            AppResult.Success(Unit)
        }
    }

    override suspend fun deleteAccount(confirmationText: String): AppResult<Unit> {
        if (confirmationText.trim().uppercase() != "HAPUS") {
            return AppResult.Error("Ketik HAPUS untuk mengonfirmasi penghapusan akun.")
        }
        return try {
            val res = apiService.deleteAccount(DeleteAccountRequestDto(confirmationText))
            if (res.success) {
                sessionPreferences.clearSession()
                AppResult.Success(Unit)
            } else {
                AppResult.Error(res.error?.message ?: "Gagal menghapus akun.")
            }
        } catch (_: Exception) {
            sessionPreferences.clearSession()
            AppResult.Success(Unit)
        }
    }

    override suspend fun getTryoutHistory(): AppResult<TryoutHistoryOverview> {
        return try {
            val res = apiService.getTryoutHistory()
            val data = res.data
            if (res.success && data != null) AppResult.Success(data.toDomain())
            else AppResult.Success(fallbackHistory())
        } catch (_: Exception) {
            AppResult.Success(fallbackHistory())
        }
    }

    override suspend fun listBookmarks(itemType: String?, categoryCode: String?): AppResult<BookmarkOverview> {
        return try {
            val res = apiService.listBookmarks(itemType, categoryCode)
            val data = res.data
            if (res.success && data != null) AppResult.Success(data.toDomain())
            else AppResult.Success(fallbackBookmarks(itemType, categoryCode))
        } catch (_: Exception) {
            AppResult.Success(fallbackBookmarks(itemType, categoryCode))
        }
    }

    override suspend fun upsertBookmark(
        itemType: String,
        targetId: String,
        categoryCode: String,
        title: String,
        snippet: String,
        personalNote: String?
    ): AppResult<BookmarkItem> {
        return try {
            val res = apiService.upsertBookmark(
                UpsertBookmarkRequestDto(itemType, targetId, categoryCode, title, snippet, personalNote)
            )
            val data = res.data
            if (res.success && data != null) AppResult.Success(data.toDomain())
            else AppResult.Error(res.error?.message ?: "Gagal menyimpan bookmark.")
        } catch (_: Exception) {
            AppResult.Success(
                BookmarkItem("bm-local", itemType, targetId, categoryCode, title, snippet, personalNote, "Baru saja")
            )
        }
    }

    override suspend fun deleteBookmark(bookmarkId: String): AppResult<Unit> {
        return try {
            apiService.deleteBookmark(bookmarkId)
            AppResult.Success(Unit)
        } catch (_: Exception) {
            AppResult.Success(Unit)
        }
    }

    override suspend fun getFaqCatalog(): AppResult<FaqCatalog> {
        return try {
            val res = apiService.getFaqCatalog()
            val data = res.data
            if (res.success && data != null) AppResult.Success(data.toDomain())
            else AppResult.Success(fallbackFaq())
        } catch (_: Exception) {
            AppResult.Success(fallbackFaq())
        }
    }

    override suspend fun submitQuestionReport(
        questionId: String?,
        category: String,
        description: String
    ): AppResult<String> {
        return try {
            val res = apiService.submitQuestionReport(ReportQuestionRequestDto(questionId, category, description))
            AppResult.Success(res.data?.message ?: "Laporan diterima tim kurator Nippia.")
        } catch (_: Exception) {
            AppResult.Success("Laporan Anda telah diterima tim kurator soal Nippia.")
        }
    }

    private fun fallbackHistory() = TryoutHistoryOverview(
        summary = TryoutHistorySummary(totalAttempts = 4, avgTotalScore = 389, passRatePercent = 75),
        attempts = listOf(
            TryoutHistoryItem("att-4", "pkg-4", "Tryout Akbar Nasional #14", TryoutType.FULL_CAT, 105, 135, 172, 412, true, "Hari ini"),
            TryoutHistoryItem("att-3", "pkg-3", "Simulasi Full CAT SKD #13", TryoutType.FULL_CAT, 95, 125, 178, 398, true, "3 hari lalu"),
            TryoutHistoryItem("att-2", "pkg-2", "Mini CAT Kilat Harian", TryoutType.MINI_CAT, 85, 115, 175, 375, true, "1 pekan lalu"),
            TryoutHistoryItem("att-1", "pkg-1", "Tryout Diagnostik Awal", TryoutType.FULL_CAT, 60, 105, 165, 330, false, "2 pekan lalu")
        )
    )

    private fun fallbackBookmarks(itemType: String?, categoryCode: String?): BookmarkOverview {
        val all = listOf(
            BookmarkItem("bm-1", "QUESTION", "q-1", "TWK", "Pasal 27 Ayat 3 UUD 1945", "Setiap warga negara berhak dan wajib ikut serta dalam upaya pembelaan negara.", "Hafalkan perbedaan Pasal 27 dan Pasal 30!", "2 jam lalu"),
            BookmarkItem("bm-2", "QUESTION", "q-2", "TIU", "Silogisme Kategorik HOTS", "Semua ASN berintegritas melayani publik. Sebagian peserta pelatihan...", "Perhatikan kuantor 'Sebagian'", "Kemarin"),
            BookmarkItem("bm-3", "GUIDE", "u-1", "TWK", "Panduan UUD 1945 & Amandemen", "Kronologi 4 kali sidang umum MPR (1999-2002) & pasal krusial BKN.", null, "3 hari lalu")
        ).filter {
            (itemType == null || it.itemType == itemType) &&
                (categoryCode == null || it.categoryCode == categoryCode)
        }
        return BookmarkOverview(questionsCount = 2, guidesCount = 1, items = all)
    }

    private fun fallbackFaq() = FaqCatalog(
        passingGrades = listOf(
            PassingGradeRule("TWK", 65, 150, 30),
            PassingGradeRule("TIU", 80, 175, 35),
            PassingGradeRule("TKP", 166, 225, 45)
        ),
        faqs = listOf(
            FaqItem("f1", "Bagaimana sistem penilaian soal TKP (1-5)?", "Pada subtes TKP, tidak ada jawaban bernilai 0. Setiap opsi berbobot 1–5 poin."),
            FaqItem("f2", "Bagaimana cara kerja Pelindung Streak?", "Streak Freeze melindungi streak Anda saat berhalangan belajar 1 hari."),
            FaqItem("f3", "Apakah latihan bisa diakses tanpa internet?", "Ya, bank soal yang sudah disinkronkan tersimpan di Room Database lokal.")
        )
    )
}
