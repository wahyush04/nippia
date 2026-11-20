package id.my.kulitech.nippia.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.my.kulitech.nippia.data.local.NippiaDatabase
import id.my.kulitech.nippia.data.local.dao.SyncMetadataDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val PREFERENCES_NAME = "nippia_preferences"

    @Provides
    @Singleton
    fun provideNippiaDatabase(
        @ApplicationContext context: Context
    ): NippiaDatabase = Room.databaseBuilder(
        context,
        NippiaDatabase::class.java,
        NippiaDatabase.DATABASE_NAME
    )
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    @Provides
    fun provideSyncMetadataDao(database: NippiaDatabase): SyncMetadataDao =
        database.syncMetadataDao()

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile(PREFERENCES_NAME) }
    )
}
