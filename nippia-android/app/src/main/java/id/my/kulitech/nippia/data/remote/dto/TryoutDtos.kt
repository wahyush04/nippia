package id.my.kulitech.nippia.data.remote.dto

import id.my.kulitech.nippia.domain.model.ScheduledTryoutEvent
import id.my.kulitech.nippia.domain.model.TryoutAttemptSummary
import id.my.kulitech.nippia.domain.model.TryoutCatalog
import id.my.kulitech.nippia.domain.model.TryoutPackageItem
import id.my.kulitech.nippia.domain.model.TryoutType
import kotlinx.serialization.Serializable

@Serializable
data class TryoutAttemptSummaryDto(
    val id: String,
    val twkScore: Int,
    val tiuScore: Int,
    val tkpScore: Int,
    val totalScore: Int,
    val isPassed: Boolean,
    val submittedAt: String? = null
) {
    fun toDomain() = TryoutAttemptSummary(
        id = id,
        twkScore = twkScore,
        tiuScore = tiuScore,
        tkpScore = tkpScore,
        totalScore = totalScore,
        isPassed = isPassed,
        submittedAt = submittedAt
    )
}

@Serializable
data class TryoutPackageDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val type: String,
    val durationMinutes: Int,
    val totalQuestions: Int,
    val isProOnly: Boolean = false,
    val lastAttempt: TryoutAttemptSummaryDto? = null
) {
    fun toDomain() = TryoutPackageItem(
        id = id,
        title = title,
        description = description.orEmpty(),
        type = TryoutType.fromApi(type),
        durationMinutes = durationMinutes,
        totalQuestions = totalQuestions,
        isProOnly = isProOnly,
        lastAttempt = lastAttempt?.toDomain()
    )
}

@Serializable
data class ScheduledTryoutEventDto(
    val id: String,
    val title: String,
    val scheduleLabel: String,
    val participantsCount: Int,
    val durationMinutes: Int,
    val totalQuestions: Int
) {
    fun toDomain() = ScheduledTryoutEvent(
        id = id,
        title = title,
        scheduleLabel = scheduleLabel,
        participantsCount = participantsCount,
        durationMinutes = durationMinutes,
        totalQuestions = totalQuestions
    )
}

@Serializable
data class TryoutCatalogDataDto(
    val packages: List<TryoutPackageDto> = emptyList(),
    val scheduledEvent: ScheduledTryoutEventDto? = null
) {
    fun toDomain() = TryoutCatalog(
        packages = packages.map { it.toDomain() },
        scheduledEvent = scheduledEvent?.toDomain() ?: ScheduledTryoutEvent(
            id = "akbar-nasional-12",
            title = "Tryout Akbar Nasional SKD #12",
            scheduleLabel = "Sabtu, 29 Maret 2025 • 09.00 - 10.40 WIB",
            participantsCount = 1280,
            durationMinutes = 100,
            totalQuestions = 110
        )
    )
}
