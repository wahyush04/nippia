package id.my.kulitech.nippia.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.my.kulitech.nippia.data.local.entity.SyncMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncMetadataDao {
    @Query("SELECT * FROM sync_metadata WHERE `key` = :key LIMIT 1")
    fun observeByKey(key: String): Flow<SyncMetadataEntity?>

    @Query("SELECT * FROM sync_metadata WHERE `key` = :key LIMIT 1")
    suspend fun getByKey(key: String): SyncMetadataEntity?

    @Upsert
    suspend fun upsert(entity: SyncMetadataEntity)
}
