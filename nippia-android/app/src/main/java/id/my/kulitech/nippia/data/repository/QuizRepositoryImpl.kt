package id.my.kulitech.nippia.data.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.data.remote.api.QuizApiService
import id.my.kulitech.nippia.data.remote.dto.AnswerSubmissionDto
import id.my.kulitech.nippia.data.remote.dto.CompleteUnitRequestDto
import id.my.kulitech.nippia.domain.model.QuestionPresentationParser
import id.my.kulitech.nippia.domain.model.QuizAnswerSubmission
import id.my.kulitech.nippia.domain.model.QuizOption
import id.my.kulitech.nippia.domain.model.QuizQuestion
import id.my.kulitech.nippia.domain.model.QuizQuestionType
import id.my.kulitech.nippia.domain.model.QuizUnitSession
import id.my.kulitech.nippia.domain.model.UnitCompletionResult
import id.my.kulitech.nippia.domain.repository.QuizRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepositoryImpl @Inject constructor(
    private val quizApiService: QuizApiService
) : QuizRepository {

    override suspend fun getUnitQuestions(
        unitId: String,
        fallbackCategory: String
    ): AppResult<QuizUnitSession> {
        return try {
            val response = quizApiService.getUnitQuestions(unitId)
            val data = response.data
            if (response.success && data != null && data.questions.isNotEmpty()) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Success(fallbackSession(unitId, fallbackCategory))
            }
        } catch (_: Exception) {
            AppResult.Success(fallbackSession(unitId, fallbackCategory))
        }
    }

    override suspend fun completeUnit(
        unitId: String,
        answers: List<QuizAnswerSubmission>,
        heartsLost: Int,
        durationSeconds: Int
    ): AppResult<UnitCompletionResult> {
        return try {
            val request = CompleteUnitRequestDto(
                answers = answers.map { AnswerSubmissionDto(it.questionId, it.selectedOptionId) },
                heartsLost = heartsLost.coerceIn(0, 5),
                durationSeconds = durationSeconds.coerceAtLeast(1),
                completedAt = Instant.now().toString()
            )
            val response = quizApiService.completeUnit(
                unitId = unitId,
                idempotencyKey = UUID.randomUUID().toString(),
                request = request
            )
            val data = response.data
            if (response.success && data != null) {
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Success(UnitCompletionResult(15, 65, 1, (5 - heartsLost).coerceAtLeast(0), null))
            }
        } catch (_: Exception) {
            // Offline-resilient fallback so user can complete unit cleanly
            AppResult.Success(UnitCompletionResult(15, 65, 1, (5 - heartsLost).coerceAtLeast(0), null))
        }
    }

    private fun fallbackSession(unitId: String, category: String): QuizUnitSession {
        val sampleQuestions = listOf(
            buildQuestion(
                id = "q-twk-bela",
                categoryCode = "TWK",
                subTopic = "Pilar Negara & Bela Negara",
                type = QuizQuestionType.MULTIPLE_CHOICE,
                orderIndex = 1,
                contentText = """
                    [KONTEKS: Konteks SKD BKN • UU No. 23 Tahun 2019]
                    [PETUNJUK: Fokus pada aksi nyata ASN yang berdampak positif pada stabilitas informasi nasional.]
                    Dalam rangka mempertahankan keutuhan dan kedaulatan NKRI dari ancaman siber dan disinformasi global, seorang ASN dituntut mengamalkan nilai dasar Bela Negara.
                    Tindakan nyata yang paling mencerminkan nilai "Rela Berkorban untuk Bangsa dan Negara" di era transformasi digital adalah...
                """.trimIndent(),
                explanation = "Menyediakan waktu di luar jam kerja untuk memverifikasi fakta, menangkal hoaks, dan mengedukasi masyarakat mencerminkan pengorbanan waktu dan tenaga demi kepentingan bangsa.",
                options = listOf(
                    QuizOption("o1", "A", "Membagikan setiap berita viral di media sosial pribadi tanpa memverifikasi sumbernya.", null, 0),
                    QuizOption("o2", "B", "Menyediakan waktu di luar jam kerja untuk memverifikasi fakta dan menangkal hoaks secara proaktif.", null, 5),
                    QuizOption("o3", "C", "Bersikap pasif dan tidak menggunakan media sosial sama sekali.", null, 0),
                    QuizOption("o4", "D", "Menyumbangkan seluruh gaji bulanan ke lembaga internasional.", null, 0),
                    QuizOption("o5", "E", "Melaporkan akun media sosial rekan kerja yang berbeda pendapat secara sepihak.", null, 0)
                )
            ),
            buildQuestion(
                id = "q-tiu-silogisme",
                categoryCode = "TIU",
                subTopic = "Logika Penalaran",
                type = QuizQuestionType.MULTIPLE_CHOICE,
                orderIndex = 2,
                contentText = """
                    [KONTEKS: Kisi TIU • Hukum Silogisme Kategorik]
                    [PETUNJUK: Premis Universal (+) bertemu Partikular (-) menghasilkan simpulan Partikular Negatif.]
                    P1: Semua aparatur sipil negara yang berintegritas tinggi selalu menolak gratifikasi dalam bentuk apa pun.
                    P2: Sebagian pejabat pengadaan barang dan jasa menerima bingkisan berharga dari rekanan proyek.
                    Kesimpulan logis dan sah yang paling tepat berdasarkan kedua premis di atas adalah...
                """.trimIndent(),
                explanation = "Premis 1 universal positif dan Premis 2 partikular negatif (menerima bingkisan bertentangan dengan menolak gratifikasi). Kesimpulan mengikuti premis terlemah: sebagian pejabat pengadaan bukan ASN yang berintegritas tinggi.",
                options = listOf(
                    QuizOption("o6", "A", "Sebagian pejabat pengadaan barang dan jasa bukan ASN yang berintegritas tinggi.", null, 5),
                    QuizOption("o7", "B", "Semua pejabat pengadaan barang dan jasa menolak segala bentuk gratifikasi.", null, 0),
                    QuizOption("o8", "C", "Sebagian ASN berintegritas tinggi menerima bingkisan proyek.", null, 0),
                    QuizOption("o9", "D", "Semua rekanan proyek wajib memberikan bingkisan.", null, 0),
                    QuizOption("o10", "E", "Tidak ada hubungan antara penerimaan bingkisan dan integritas.", null, 0)
                )
            ),
            buildQuestion(
                id = "q-tiu-analitis",
                categoryCode = "TIU",
                subTopic = "Penalaran Analitis",
                type = QuizQuestionType.MULTIPLE_CHOICE,
                orderIndex = 3,
                contentText = """
                    [DENAH: Denah Ruang Ujian CAT | Meja 1=Andi | Meja 2=Citra | Meja 3=Budi | Meja 4=? | Meja 5=Eka]
                    Lima orang calon pegawai (Andi, Budi, Citra, Dodi, dan Eka) duduk berjajar dalam ruang tes CAT. Citra duduk di antara Andi dan Budi. Dodi tidak boleh duduk berdampingan dengan Andi.
                    Jika Eka berada di posisi paling kanan, siapakah yang duduk tepat di sebelah kiri Eka?
                """.trimIndent(),
                explanation = "Andi-Citra-Budi menempati Meja 1, 2, dan 3. Karena Eka berada di Meja 5 paling kanan, maka meja tepat di sebelah kiri Eka adalah Meja 4 yang wajib diisi oleh Dodi.",
                options = listOf(
                    QuizOption("o11", "A", "Andi", null, 0),
                    QuizOption("o12", "B", "Budi", null, 0),
                    QuizOption("o13", "C", "Dodi", null, 5),
                    QuizOption("o14", "D", "Citra", null, 0),
                    QuizOption("o15", "E", "Tidak dapat ditentukan", null, 0)
                )
            ),
            buildQuestion(
                id = "q-tkp-pelayanan",
                categoryCode = "TKP",
                subTopic = "Jejaring Kerja & Pelayanan Publik",
                type = QuizQuestionType.WEIGHTED_SCALE,
                orderIndex = 4,
                contentText = """
                    [KONTEKS: Studi Kasus Dilematis • Skala Poin 1–5]
                    Anda adalah koordinator tim pelayanan perizinan terpadu. Saat antrean pemohon padat, sistem verifikasi berkas digital mendadak gangguan selama 2 jam dan pemohon mulai kecewa.
                    Sebagai penanggung jawab loket, langkah paling efektif yang Anda prioritaskan adalah...
                """.trimIndent(),
                explanation = "Solusi terbaik menggabungkan layanan kontingensi (pencatatan fisik & antrean manual) dengan komunikasi empatik serta transparansi estimasi perbaikan.",
                options = listOf(
                    QuizOption("o16", "A", "Beralih sementara ke pencatatan berkas fisik, membagikan nomor antrean manual, dan menyampaikan permohonan maaf secara santun.", null, 5),
                    QuizOption("o17", "B", "Meminta seluruh pemohon pulang dan kembali besok hari.", null, 1),
                    QuizOption("o18", "C", "Menunggu teknisi selesai sambil menenangkan pemohon di ruang tunggu.", null, 3),
                    QuizOption("o19", "D", "Menerima berkas tanpa pemeriksaan kelengkapan awal.", null, 2),
                    QuizOption("o20", "E", "Menghubungi atasan dan menunggu arahan tertulis sebelum bertindak.", null, 4)
                )
            )
        )
        val prioritized = sampleQuestions.sortedBy { if (it.categoryCode.equals(category, ignoreCase = true)) 0 else 1 }
        return QuizUnitSession(
            unitId = unitId,
            title = "Latihan Mikro $category",
            subTopic = "SKD Adaptif",
            xpReward = 15,
            questions = prioritized
        )
    }

    private fun buildQuestion(
        id: String,
        categoryCode: String,
        subTopic: String,
        type: QuizQuestionType,
        orderIndex: Int,
        contentText: String,
        explanation: String,
        options: List<QuizOption>
    ): QuizQuestion {
        val parsed = QuestionPresentationParser.parse(contentText)
        return QuizQuestion(
            id = id,
            categoryCode = categoryCode,
            subTopic = subTopic,
            type = type,
            orderIndex = orderIndex,
            contentText = contentText,
            imageUrl = null,
            explanation = explanation,
            hint = parsed.hint,
            presentation = parsed.presentation,
            options = options
        )
    }
}
