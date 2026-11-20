package id.my.kulitech.nippia.core.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    @Upsert
    suspend fun upsertUnits(units: List<UnitEntity>)

    @Upsert
    suspend fun upsertQuestions(questions: List<QuestionEntity>)

    @Upsert
    suspend fun upsertOptions(options: List<QuestionOptionEntity>)

    @Query("SELECT * FROM units WHERE status = 'PUBLISHED' ORDER BY orderIndex ASC")
    fun observePublishedUnits(): Flow<List<UnitEntity>>

    @Query("SELECT * FROM questions WHERE unitId = :unitId AND status = 'PUBLISHED' ORDER BY orderIndex ASC")
    suspend fun getPublishedQuestionsForUnit(unitId: String): List<QuestionEntity>

    @Query("SELECT * FROM question_options WHERE questionId IN (:questionIds) ORDER BY label ASC")
    suspend fun getOptionsForQuestions(questionIds: List<String>): List<QuestionOptionEntity>

    @Upsert
    suspend fun enqueuePendingSync(item: PendingSyncEntity)

    @Query("SELECT * FROM pending_sync ORDER BY createdAtEpochMs ASC")
    suspend fun getAllPendingSync(): List<PendingSyncEntity>

    @Query("DELETE FROM pending_sync WHERE idempotencyKey = :idempotencyKey")
    suspend fun deletePendingSync(idempotencyKey: String)
}
