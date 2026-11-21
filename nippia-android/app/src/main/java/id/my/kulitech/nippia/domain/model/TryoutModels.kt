package id.my.kulitech.nippia.domain.model

enum class TryoutType {
    MINI_CAT,
    FULL_CAT;

    companion object {
        fun fromApi(value: String): TryoutType =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: MINI_CAT
    }
}

data class TryoutAttemptSummary(
    val id: String,
    val twkScore: Int,
    val tiuScore: Int,
    val tkpScore: Int,
    val totalScore: Int,
    val isPassed: Boolean,
    val submittedAt: String?
)

data class TryoutPackageItem(
    val id: String,
    val title: String,
    val description: String,
    val type: TryoutType,
    val durationMinutes: Int,
    val totalQuestions: Int,
    val isProOnly: Boolean,
    val lastAttempt: TryoutAttemptSummary?
)

data class ScheduledTryoutEvent(
    val id: String,
    val title: String,
    val scheduleLabel: String,
    val participantsCount: Int,
    val durationMinutes: Int,
    val totalQuestions: Int
)

data class TryoutCatalog(
    val packages: List<TryoutPackageItem>,
    val scheduledEvent: ScheduledTryoutEvent
)
