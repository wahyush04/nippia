package id.my.kulitech.nippia.domain.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.AuthSession
import id.my.kulitech.nippia.domain.model.SessionState

interface AuthRepository {
    suspend fun checkSession(): SessionState
    suspend fun loginWithGoogle(idToken: String, fcmToken: String? = null): AppResult<AuthSession>
    suspend fun loginAsGuest(fcmToken: String? = null): AppResult<AuthSession>
    suspend fun linkGoogleAccount(idToken: String): AppResult<AuthSession>
    suspend fun logout()
}
