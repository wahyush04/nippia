package id.my.kulitech.nippia.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "units")
data class UnitEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val sectionTitle: String,
    val orderIndex: Int,
    val title: String,
    val subTopic: String?,
    val guideMarkdown: String,
    val xpReward: Int,
    val isProOnly: Boolean,
    val status: String,
    val version: Int,
    val updatedAt: String,
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val unitId: String?,
    val categoryCode: String,
    val subTopic: String,
    val type: String,
    val orderIndex: Int,
    val contentText: String,
    val imageUrl: String?,
    val explanation: String,
    val status: String,
    val updatedAt: String,
)

@Entity(tableName = "question_options")
data class QuestionOptionEntity(
    @PrimaryKey val id: String,
    val questionId: String,
    val label: String,
    val optionText: String,
    val imageUrl: String?,
    val score: Int,
)

@Entity(tableName = "pending_sync")
data class PendingSyncEntity(
    @PrimaryKey val idempotencyKey: String,
    val endpoint: String,
    val payloadJson: String,
    val createdAtEpochMs: Long,
)
