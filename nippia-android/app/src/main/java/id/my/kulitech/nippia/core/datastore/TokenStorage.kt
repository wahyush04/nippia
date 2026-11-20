package id.my.kulitech.nippia.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.nippiaDataStore by preferencesDataStore(name = "nippia_prefs")

@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val LAST_CONTENT_SYNC = stringPreferencesKey("last_content_sync")
        val IS_PRO = booleanPreferencesKey("is_pro")
    }

    val accessTokenFlow: Flow<String?> = context.nippiaDataStore.data.map { it[Keys.ACCESS_TOKEN] }
    val isLoggedInFlow: Flow<Boolean> = accessTokenFlow.map { !it.isNullOrBlank() }

    suspend fun getAccessToken(): String? = context.nippiaDataStore.data.first()[Keys.ACCESS_TOKEN]
    suspend fun getRefreshToken(): String? = context.nippiaDataStore.data.first()[Keys.REFRESH_TOKEN]
    suspend fun getLastContentSync(): String? = context.nippiaDataStore.data.first()[Keys.LAST_CONTENT_SYNC]

    suspend fun saveSession(
        accessToken: String,
        refreshToken: String,
        userId: String,
        isPro: Boolean,
    ) {
        context.nippiaDataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = accessToken
            prefs[Keys.REFRESH_TOKEN] = refreshToken
            prefs[Keys.USER_ID] = userId
            prefs[Keys.IS_PRO] = isPro
        }
    }

    suspend fun updateLastContentSync(timestamp: String) {
        context.nippiaDataStore.edit { prefs ->
            prefs[Keys.LAST_CONTENT_SYNC] = timestamp
        }
    }

    suspend fun clearSession() {
        context.nippiaDataStore.edit { prefs ->
            prefs.remove(Keys.ACCESS_TOKEN)
            prefs.remove(Keys.REFRESH_TOKEN)
            prefs.remove(Keys.USER_ID)
            prefs.remove(Keys.IS_PRO)
        }
    }
}
