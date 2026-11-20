package id.my.kulitech.nippia.domain.model

enum class FormationCategory(val apiValue: String?, val label: String) {
    ALL(null, "Semua"),
    KEMENTERIAN("KEMENTERIAN", "Kementerian"),
    LEMBAGA("LEMBAGA", "Lembaga Negara"),
    PEMDA("PEMDA", "Pemda / Prov"),
    HUKUM("HUKUM", "Kejaksaan / Hukum")
}

data class PassingGradeInfo(
    val twk: Int,
    val tiu: Int,
    val tkp: Int,
    val totalMin: Int
)

data class CpnsFormation(
    val id: String,
    val institution: String,
    val position: String,
    val category: String,
    val qualification: String,
    val badge: String?,
    val passingGrade: PassingGradeInfo,
    val safeTargetScore: Int,
    val competitionRatio: String
)

enum class DailyGoalOption(
    val apiValue: String,
    val title: String,
    val xpPerDay: Int,
    val durationLabel: String,
    val description: String,
    val isRecommended: Boolean = false
) {
    SANTAI(
        apiValue = "SANTAI",
        title = "Santai",
        xpPerDay = 20,
        durationLabel = "~3 menit per hari",
        description = "1 unit kuis mikro • Cocok untuk pemula & jadwal dinas padat."
    ),
    REGULER(
        apiValue = "REGULER",
        title = "Reguler",
        xpPerDay = 50,
        durationLabel = "~8 menit per hari",
        description = "2–3 unit kuis mikro • Paling ideal untuk retensi materi jangka panjang.",
        isRecommended = true
    ),
    AMBISIUS(
        apiValue = "AMBISIUS",
        title = "Ambisius",
        xpPerDay = 100,
        durationLabel = "~15 menit per hari",
        description = "5 unit kuis + 1 evaluasi • Kejar target NIP & 10 besar liga."
    )
}

data class DiagnosticOption(
    val label: String,
    val text: String,
    val score: Int
)

data class DiagnosticQuestion(
    val id: String,
    val categoryCode: String,
    val topicLabel: String,
    val questionType: String,
    val contentText: String,
    val explanation: String,
    val options: List<DiagnosticOption>
)

data class DiagnosticScore(
    val twkCorrect: Boolean,
    val tiuCorrect: Boolean,
    val tkpScore: Int
)

data class OnboardingSubmissionResult(
    val onboardingCompleted: Boolean,
    val bonusXp: Int,
    val bonusGems: Int
)
