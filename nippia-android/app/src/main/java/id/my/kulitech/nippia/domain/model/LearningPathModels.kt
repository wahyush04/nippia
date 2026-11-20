package id.my.kulitech.nippia.domain.model

enum class SkdCategoryCode(
    val code: String,
    val shortTitle: String,
    val fullName: String,
    val badgeLabel: String,
    val subtitle: String,
    val topics: List<String>
) {
    TWK(
        code = "TWK",
        shortTitle = "TWK",
        fullName = "Tes Wawasan Kebangsaan",
        badgeLabel = "Rekomendasi Pemula",
        subtitle = "Tes Wawasan Kebangsaan • Pilar RI",
        topics = listOf("Nasionalisme", "Integritas", "Bela Negara", "Pilar UUD 1945", "Bahasa Negara")
    ),
    TIU(
        code = "TIU",
        shortTitle = "TIU",
        fullName = "Tes Intelegensia Umum",
        badgeLabel = "Tantangan Logika",
        subtitle = "Tes Intelegensia Umum • Logika & Numerik",
        topics = listOf("Silogisme", "Analogi Verbal", "Deret Angka", "Hitung Cepat", "Figural 2D")
    ),
    TKP(
        code = "TKP",
        shortTitle = "TKP",
        fullName = "Tes Karakteristik Pribadi",
        badgeLabel = "Poin Tertinggi SKD",
        subtitle = "Tes Karakteristik Pribadi • Skala Skor 1–5",
        topics = listOf("Pelayanan Publik", "Jejaring Kerja", "Sosial Budaya", "TIK", "Anti Radikalisme")
    );

    companion object {
        fun fromCode(code: String?): SkdCategoryCode =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: TWK
    }
}

enum class LearningUnitStatus {
    LOCKED,
    ACTIVE,
    COMPLETED;

    companion object {
        fun fromApi(value: String): LearningUnitStatus =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: LOCKED
    }
}

data class LearningUnit(
    val id: String,
    val categoryId: String,
    val sectionTitle: String,
    val orderIndex: Int,
    val title: String,
    val subTopic: String,
    val guideMarkdown: String,
    val xpReward: Int,
    val isProOnly: Boolean,
    val status: LearningUnitStatus,
    val bestScore: Int
)

data class LearningCategoryPath(
    val id: String,
    val code: SkdCategoryCode,
    val name: String,
    val passingGrade: Int,
    val units: List<LearningUnit>
) {
    val completedCount: Int get() = units.count { it.status == LearningUnitStatus.COMPLETED }
    val totalCount: Int get() = units.size
    val progressFraction: Float get() = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount
}

data class LearningPathOverview(
    val pendingMistakesCount: Int,
    val gamification: GamificationSummary,
    val categories: List<LearningCategoryPath>
)
