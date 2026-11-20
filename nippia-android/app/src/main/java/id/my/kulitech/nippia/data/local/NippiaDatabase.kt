package id.my.kulitech.nippia.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import id.my.kulitech.nippia.data.local.dao.SyncMetadataDao
import id.my.kulitech.nippia.data.local.entity.SyncMetadataEntity

/**
 * Offline-first Room database serving as the local Single Source of Truth (SSOT) for Nippia.
 */
@Database(
    entities = [
        SyncMetadataEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NippiaDatabase : RoomDatabase() {
    abstract fun syncMetadataDao(): SyncMetadataDao

    companion object {
        const val DATABASE_NAME = "nippia_database"
    }
}
