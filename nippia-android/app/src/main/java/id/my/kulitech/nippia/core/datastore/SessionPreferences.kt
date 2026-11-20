package id.my.kulitech.nippia.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages persisted user session credentials, sync timestamps, and subscription cache
 * backed by Jetpack DataStore.
 */
@Singleton
class SessionPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val LAST_CONTENT_SYNC = stringPreferencesKey("last_content_sync")
        val IS_PRO_USER = booleanPreferencesKey("is_pro_user")
        val SELECTED_CATEGORY = stringPreferencesKey("selected_category")
    }

    val accessToken: Flow<String?> = dataStore.data.map { it[Keys.ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = dataStore.data.map { it[Keys.REFRESH_TOKEN] }
    val lastContentSync: Flow<String?> = dataStore.data.map { it[Keys.LAST_CONTENT_SYNC] }
    val isProUser: Flow<Boolean> = dataStore.data.map { it[Keys.IS_PRO_USER] ?: false }
    val selectedCategory: Flow<String> = dataStore.data.map { it[Keys.SELECTED_CATEGORY] ?: "TWK" }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = accessToken
            prefs[Keys.REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun updateLastContentSync(isoTimestamp: String) {
        dataStore.edit { prefs ->
            prefs[Keys.LAST_CONTENT_SYNC] = isoTimestamp
        }
    }

    suspend fun setProStatus(isPro: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.IS_PRO_USER] = isPro
        }
    }

    suspend fun setSelectedCategory(categoryCode: String) {
        dataStore.edit { prefs ->
            prefs[Keys.SELECTED_CATEGORY] = categoryCode
        }
    }

    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.ACCESS_TOKEN)
            prefs.remove(Keys.REFRESH_TOKEN)
            prefs.remove(Keys.IS_PRO_USER)
        }
    }
}
