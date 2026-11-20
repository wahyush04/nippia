package id.my.kulitech.nippia.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNippiaDatabase(
        @ApplicationContext context: Context,
    ): NippiaDatabase {
        return Room.databaseBuilder(
            context,
            NippiaDatabase::class.java,
            "nippia_offline.db",
        ).build()
    }

    @Provides
    fun provideContentDao(database: NippiaDatabase): ContentDao = database.contentDao()
}
