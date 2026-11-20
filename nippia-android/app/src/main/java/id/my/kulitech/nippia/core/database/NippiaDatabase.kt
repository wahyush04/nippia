package id.my.kulitech.nippia.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UnitEntity::class,
        QuestionEntity::class,
        QuestionOptionEntity::class,
        PendingSyncEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class NippiaDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
}
