package id.my.kulitech.nippia.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tracks local delta sync checkpoints per resource key (e.g., "content", "units").
 */
@Entity(tableName = "sync_metadata")
data class SyncMetadataEntity(
    @PrimaryKey
    val key: String,
    val lastSyncedAtIso: String
)
