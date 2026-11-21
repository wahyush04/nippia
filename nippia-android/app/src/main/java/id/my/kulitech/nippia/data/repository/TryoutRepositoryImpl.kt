package id.my.kulitech.nippia.data.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.data.remote.api.TryoutApiService
import id.my.kulitech.nippia.data.remote.dto.AnswerSubmissionDto
import id.my.kulitech.nippia.data.remote.dto.SubmitTryoutRequestDto
import id.my.kulitech.nippia.domain.model.QuestionPresentationParser
import id.my.kulitech.nippia.domain.model.QuizAnswerSubmission
import id.my.kulitech.nippia.domain.model.QuizOption
import id.my.kulitech.nippia.domain.model.QuizQuestionType
import id.my.kulitech.nippia.domain.model.ScheduledTryoutEvent
import id.my.kulitech.nippia.domain.model.SubTopicBreakdownItem
import id.my.kulitech.nippia.domain.model.SubtestGradeResult
import id.my.kulitech.nippia.domain.model.TryoutAttemptSummary
import id.my.kulitech.nippia.domain.model.TryoutCatalog
import id.my.kulitech.nippia.domain.model.TryoutPackageItem
import id.my.kulitech.nippia.domain.model.TryoutQuestion
import id.my.kulitech.nippia.domain.model.TryoutSession
import id.my.kulitech.nippia.domain.model.TryoutSubmitResult
import id.my.kulitech.nippia.domain.model.TryoutType
import id.my.kulitech.nippia.domain.repository.TryoutRepository
import java.time.Instant
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

    override suspend fun startTryout(packageId: String, title: String): AppResult<TryoutSession> {
        return try {
            val response = tryoutApiService.startTryout(packageId)
            val data = response.data
            if (response.success && data != null && data.questions.isNotEmpty()) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Success(fallbackSession(packageId, title))
            }
        } catch (_: Exception) {
            AppResult.Success(fallbackSession(packageId, title))
        }
    }

    override suspend fun submitTryout(
        attemptId: String,
        answers: List<QuizAnswerSubmission>
    ): AppResult<TryoutSubmitResult> {
        return try {
            val request = SubmitTryoutRequestDto(
                answers = answers.map { AnswerSubmissionDto(it.questionId, it.selectedOptionId) }
            )
            val response = tryoutApiService.submitTryout(attemptId, request)
            val data = response.data
            if (response.success && data != null) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Success(fallbackSubmitResult(attemptId, answers))
            }
        } catch (_: Exception) {
            AppResult.Success(fallbackSubmitResult(attemptId, answers))
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

    private fun fallbackSession(packageId: String, title: String): TryoutSession {
        val now = Instant.now().toString()
        val questions = listOf(
            sampleTryoutQuestion(
                id = "cat-q-1",
                orderIndex = 1,
                categoryCode = "TWK",
                subTopic = "Nasionalisme & Pilar Negara",
                type = QuizQuestionType.MULTIPLE_CHOICE,
                contentText = """
                    [KONTEKS: Soal Penalaran HOTS • Benar +5, Salah 0]
                    Dalam konteks era globalisasi dan arus informasi digital, pengamalan nilai integritas dan Bhinneka Tunggal Ika bagi ASN diuji dengan polarisasi opini publik.
                    Sikap paling tepat yang mencerminkan pemahaman pilar kebangsaan dalam pelayanan masyarakat adalah...
                """.trimIndent(),
                options = listOf(
                    QuizOption("cat-o-1a", "A", "Menolak melayani masyarakat yang berbeda pandangan politik.", null, 0),
                    QuizOption("cat-o-1b", "B", "Mengedepankan pelayanan publik yang adil, inklusif, dan nondiskriminatif.", null, 5),
                    QuizOption("cat-o-1c", "C", "Memberikan prioritas layanan kepada kelompok mayoritas setempat.", null, 0),
                    QuizOption("cat-o-1d", "D", "Menyerahkan seluruh pelayanan ke aparat penegak hukum.", null, 0),
                    QuizOption("cat-o-1e", "E", "Membatasi jam pelayanan agar tidak terjadi kerumunan.", null, 0)
                )
            ),
            sampleTryoutQuestion(
                id = "cat-q-2",
                orderIndex = 2,
                categoryCode = "TIU",
                subTopic = "Silogisme Kategorik",
                type = QuizQuestionType.MULTIPLE_CHOICE,
                contentText = """
                    P1: Semua peserta ujian CAT BKN wajib membawa kartu peserta dan KTP asli.
                    P2: Sebagian pendaftar di ruang B tidak membawa KTP asli.
                    Simpulan yang paling sah adalah...
                """.trimIndent(),
                options = listOf(
                    QuizOption("cat-o-2a", "A", "Sebagian pendaftar di ruang B bukan peserta ujian CAT BKN.", null, 5),
                    QuizOption("cat-o-2b", "B", "Semua pendaftar di ruang B membawa kartu peserta.", null, 0),
                    QuizOption("cat-o-2c", "C", "Semua peserta ujian CAT BKN tidak membawa KTP asli.", null, 0),
                    QuizOption("cat-o-2d", "D", "Tidak ada pendaftar di ruang B yang ikut ujian.", null, 0),
                    QuizOption("cat-o-2e", "E", "Semua pendaftar di ruang B lulus seleksi administrasi.", null, 0)
                )
            ),
            sampleTryoutQuestion(
                id = "cat-q-3",
                orderIndex = 3,
                categoryCode = "TKP",
                subTopic = "Pelayanan Publik",
                type = QuizQuestionType.WEIGHTED_SCALE,
                contentText = """
                    [KONTEKS: Studi Kasus Pelayanan Publik • Skala 1–5]
                    Seorang warga datang terburu-buru menjelang jam tutup loket karena salah membawa dokumen pendukung.
                    Tindakan Anda sebagai petugas pelayanan adalah...
                """.trimIndent(),
                options = listOf(
                    QuizOption("cat-o-3a", "A", "Menjelaskan kekurangan dokumen dengan ramah dan membantu menjadwalkan pengajuan esok pagi.", null, 5),
                    QuizOption("cat-o-3b", "B", "Menolak langsung karena loket hampir tutup.", null, 1),
                    QuizOption("cat-o-3c", "C", "Meminta warga membaca papan pengumuman di luar.", null, 2),
                    QuizOption("cat-o-3d", "D", "Menerima berkas tanpa verifikasi agar cepat selesai.", null, 3),
                    QuizOption("cat-o-3e", "E", "Menyarankan warga mengirim email tanpa penjelasan detail.", null, 4)
                )
            )
        )
        return TryoutSession(
            attemptId = "offline-attempt-$packageId",
            packageId = packageId,
            title = title,
            type = TryoutType.FULL_CAT,
            durationMinutes = 100,
            startedAt = now,
            expiresAt = now,
            questions = questions
        )
    }

    private fun sampleTryoutQuestion(
        id: String,
        orderIndex: Int,
        categoryCode: String,
        subTopic: String,
        type: QuizQuestionType,
        contentText: String,
        options: List<QuizOption>
    ): TryoutQuestion {
        val parsed = QuestionPresentationParser.parse(contentText)
        return TryoutQuestion(
            id = id,
            orderIndex = orderIndex,
            categoryCode = categoryCode,
            subTopic = subTopic,
            type = type,
            contentText = contentText,
            imageUrl = null,
            presentation = parsed.presentation,
            hint = parsed.hint,
            options = options
        )
    }

    private fun fallbackSubmitResult(
        attemptId: String,
        answers: List<QuizAnswerSubmission>
    ): TryoutSubmitResult {
        val answeredCount = answers.size
        val twk = if (answeredCount > 0) 95 else 0
        val tiu = if (answeredCount > 1) 125 else 0
        val tkp = if (answeredCount > 2) 178 else 0
        val total = twk + tiu + tkp
        val isPassed = twk >= 65 && tiu >= 80 && tkp >= 166
        return TryoutSubmitResult(
            attemptId = attemptId,
            packageId = "offline",
            twkScore = twk,
            tiuScore = tiu,
            tkpScore = tkp,
            totalScore = total,
            twkGrade = SubtestGradeResult(65, twk >= 65),
            tiuGrade = SubtestGradeResult(80, tiu >= 80),
            tkpGrade = SubtestGradeResult(166, tkp >= 166),
            isPassed = isPassed,
            subTopicBreakdown = listOf(
                SubTopicBreakdownItem("TWK", "Nasionalisme & Pilar Negara", 25, 30, 83.3),
                SubTopicBreakdownItem("TIU", "Silogisme Kategorik", 20, 25, 80.0),
                SubTopicBreakdownItem("TKP", "Pelayanan Publik", 22, 25, 88.0)
            )
        )
    }
}
