package id.my.kulitech.nippia.data.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.data.remote.api.OnboardingApiService
import id.my.kulitech.nippia.data.remote.dto.DiagnosticScoreRequest
import id.my.kulitech.nippia.data.remote.dto.SubmitOnboardingRequest
import id.my.kulitech.nippia.domain.model.CpnsFormation
import id.my.kulitech.nippia.domain.model.DailyGoalOption
import id.my.kulitech.nippia.domain.model.DiagnosticOption
import id.my.kulitech.nippia.domain.model.DiagnosticQuestion
import id.my.kulitech.nippia.domain.model.DiagnosticScore
import id.my.kulitech.nippia.domain.model.OnboardingSubmissionResult
import id.my.kulitech.nippia.domain.model.PassingGradeInfo
import id.my.kulitech.nippia.domain.repository.OnboardingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingRepositoryImpl @Inject constructor(
    private val onboardingApiService: OnboardingApiService
) : OnboardingRepository {

    override suspend fun getFormations(): AppResult<List<CpnsFormation>> {
        return try {
            val response = onboardingApiService.getFormations()
            val list = response.data?.formations?.map { it.toDomain() }
            if (response.success && !list.isNullOrEmpty()) {
                AppResult.Success(list)
            } else {
                AppResult.Success(fallbackFormations())
            }
        } catch (_: Exception) {
            AppResult.Success(fallbackFormations())
        }
    }

    override suspend fun getDiagnosticQuestions(): AppResult<List<DiagnosticQuestion>> {
        return try {
            val response = onboardingApiService.getDiagnosticQuestions()
            val list = response.data?.questions?.map { it.toDomain() }
            if (response.success && !list.isNullOrEmpty()) {
                AppResult.Success(list)
            } else {
                AppResult.Success(fallbackDiagnosticQuestions())
            }
        } catch (_: Exception) {
            AppResult.Success(fallbackDiagnosticQuestions())
        }
    }

    override suspend fun submitOnboarding(
        targetInstitution: String,
        targetPosition: String,
        dailyGoal: DailyGoalOption,
        dailyReminderEnabled: Boolean,
        dailyReminderTime: String,
        diagnosticCompleted: Boolean,
        diagnosticScore: DiagnosticScore?
    ): AppResult<OnboardingSubmissionResult> {
        return try {
            val request = SubmitOnboardingRequest(
                targetInstitution = targetInstitution,
                targetPosition = targetPosition,
                dailyGoal = dailyGoal.apiValue,
                dailyReminderEnabled = dailyReminderEnabled,
                dailyReminderTime = dailyReminderTime,
                diagnosticCompleted = diagnosticCompleted,
                diagnosticScore = diagnosticScore?.let {
                    DiagnosticScoreRequest(
                        twkCorrect = it.twkCorrect,
                        tiuCorrect = it.tiuCorrect,
                        tkpScore = it.tkpScore
                    )
                }
            )
            val response = onboardingApiService.submitOnboarding(request)
            val data = response.data
            if (response.success && data != null) {
                AppResult.Success(
                    OnboardingSubmissionResult(
                        onboardingCompleted = data.onboardingCompleted,
                        bonusXp = data.bonusAwarded.xp,
                        bonusGems = data.bonusAwarded.gems
                    )
                )
            } else {
                AppResult.Error(
                    message = response.error?.message ?: "Gagal menyimpan preferensi onboarding.",
                    code = response.error?.code
                )
            }
        } catch (e: Exception) {
            AppResult.Error(
                message = e.localizedMessage ?: "Tidak dapat terhubung ke server. Periksa koneksi internet Anda.",
                throwable = e
            )
        }
    }

    private fun fallbackFormations(): List<CpnsFormation> = listOf(
        CpnsFormation(
            id = "form-kemenkumham-penjaga-tahanan",
            institution = "Kementerian Hukum dan HAM",
            position = "Penjaga Tahanan / Pengelola Pemasyarakatan",
            category = "KEMENTERIAN",
            qualification = "SLTA Sederajat • D3 • S1 Semua Jurusan",
            badge = "Terpopuler #1",
            passingGrade = PassingGradeInfo(65, 80, 166, 311),
            safeTargetScore = 420,
            competitionRatio = "1 : 45 Pelamar"
        ),
        CpnsFormation(
            id = "form-kemenkeu-analis-kebijakan",
            institution = "Kementerian Keuangan (Kemenkeu)",
            position = "Analis Kebijakan / Pajak & Perbendaharaan",
            category = "KEMENTERIAN",
            qualification = "S1 Akuntansi / Ekonomi / Manajemen",
            badge = "Keketatan Ekstra",
            passingGrade = PassingGradeInfo(65, 80, 166, 311),
            safeTargetScore = 440,
            competitionRatio = "1 : 88 Pelamar"
        ),
        CpnsFormation(
            id = "form-pemprov-dki-pranata-komputer",
            institution = "Pemerintah Provinsi DKI Jakarta",
            position = "Ahli Pertama - Pranata Komputer",
            category = "PEMDA",
            qualification = "S1 Ilmu Komputer / Sistem Informasi / TI",
            badge = "TPP Tinggi",
            passingGrade = PassingGradeInfo(65, 80, 166, 311),
            safeTargetScore = 410,
            competitionRatio = "1 : 32 Pelamar"
        ),
        CpnsFormation(
            id = "form-kejaksaan-pengelola-perkara",
            institution = "Kejaksaan Agung Republik Indonesia",
            position = "Pengelola Penanganan Perkara & Arsiparis",
            category = "HUKUM",
            qualification = "SLTA Sederajat • D3 • S1 Hukum",
            badge = "Kuota Massal 2025",
            passingGrade = PassingGradeInfo(65, 80, 166, 311),
            safeTargetScore = 405,
            competitionRatio = "1 : 36 Pelamar"
        ),
        CpnsFormation(
            id = "form-bkn-analis-sdm",
            institution = "Badan Kepegawaian Negara (BKN)",
            position = "Analis SDM Aparatur Ahli Pertama",
            category = "LEMBAGA",
            qualification = "S1 Manajemen / Administrasi Publik / Psikologi",
            badge = "Instansi Pembina",
            passingGrade = PassingGradeInfo(65, 80, 166, 311),
            safeTargetScore = 415,
            competitionRatio = "1 : 40 Pelamar"
        )
    )

    private fun fallbackDiagnosticQuestions(): List<DiagnosticQuestion> = listOf(
        DiagnosticQuestion(
            id = "diag-twk-1",
            categoryCode = "TWK",
            topicLabel = "Pilar Negara • UUD 1945",
            questionType = "MULTIPLE_CHOICE",
            contentText = "Pasal 27 ayat (3) UUD 1945 menyatakan bahwa setiap warga negara berhak dan wajib ikut serta dalam upaya pembelaan negara. Prinsip ini mencerminkan kewajiban konstitusional yang berlandaskan...",
            explanation = "Upaya bela negara berlandaskan kesadaran kolektif warga negara untuk menjaga kedaulatan dan keutuhan NKRI berdasarkan Pancasila dan UUD 1945.",
            options = listOf(
                DiagnosticOption("A", "Hak prerogatif Presiden selaku kepala negara", 0),
                DiagnosticOption("B", "Kesadaran kolektif atas kedaulatan dan keutuhan NKRI", 5),
                DiagnosticOption("C", "Doktrin pertahanan militer konvensional", 0),
                DiagnosticOption("D", "Asas timbal balik kerjasama bilateral internasional", 0),
                DiagnosticOption("E", "Kewajiban pajak warga negara produktif", 0)
            )
        ),
        DiagnosticQuestion(
            id = "diag-tiu-2",
            categoryCode = "TIU",
            topicLabel = "Verbal • Silogisme",
            questionType = "MULTIPLE_CHOICE",
            contentText = "Semua ASN wajib menjunjung tinggi integritas. Sebagian peserta pelatihan CAT adalah ASN. Simpulan yang paling tepat adalah...",
            explanation = "Karena sebagian peserta pelatihan CAT adalah ASN dan seluruh ASN wajib menjunjung tinggi integritas, maka sebagian peserta pelatihan CAT wajib menjunjung tinggi integritas.",
            options = listOf(
                DiagnosticOption("A", "Semua peserta pelatihan CAT adalah ASN", 0),
                DiagnosticOption("B", "Semua ASN adalah peserta pelatihan CAT", 0),
                DiagnosticOption("C", "Sebagian peserta pelatihan CAT wajib menjunjung tinggi integritas", 5),
                DiagnosticOption("D", "Tidak ada peserta pelatihan CAT yang berintegritas", 0),
                DiagnosticOption("E", "Sebagian ASN tidak wajib menjunjung tinggi integritas", 0)
            )
        ),
        DiagnosticQuestion(
            id = "diag-tkp-3",
            categoryCode = "TKP",
            topicLabel = "Pelayanan Publik",
            questionType = "WEIGHTED_SCALE",
            contentText = "Saat Anda bertugas di loket pelayanan publik, seorang warga lansia datang kebingungan karena belum melengkapi formulir digital yang dipersyaratkan. Antrean di belakang cukup panjang. Sikap Anda adalah...",
            explanation = "Pelayanan publik berorientasi pada empati, inklusivitas (ramah lansia), dan solusi cepat tanpa mengabaikan kelancaran antrean.",
            options = listOf(
                DiagnosticOption("A", "Memintanya pulang dan melengkapi formulir terlebih dahulu bersama keluarga", 1),
                DiagnosticOption("B", "Memberikan brosur panduan pengisian agar ia mencoba sendiri di kursi tunggu", 3),
                DiagnosticOption("C", "Membantunya mengisi formulir dengan ramah sambil mengarahkan antrean lain tetap berjalan tertib", 5),
                DiagnosticOption("D", "Meminta petugas keamanan membantu mengisi formulir di luar loket", 4),
                DiagnosticOption("E", "Memintanya menunggu hingga seluruh antrean selesai baru dibantu", 2)
            )
        )
    )
}
