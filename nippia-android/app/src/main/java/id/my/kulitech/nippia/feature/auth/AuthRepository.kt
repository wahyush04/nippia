package id.my.kulitech.nippia.feature.auth

import id.my.kulitech.nippia.core.datastore.TokenStorage
import id.my.kulitech.nippia.core.network.auth.AuthApiService
import id.my.kulitech.nippia.core.network.auth.AuthResponseDto
import id.my.kulitech.nippia.core.network.auth.GoogleLoginRequest
import id.my.kulitech.nippia.core.network.auth.GuestLoginRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApiService,
    private val tokenStorage: TokenStorage,
) {
    val isLoggedInFlow = tokenStorage.isLoggedInFlow

    suspend fun loginWithGoogle(idToken: String): Result<AuthResponseDto> = runCatching {
        val response = authApi.loginWithGoogle(GoogleLoginRequest(idToken = idToken))
        val data = response.data ?: error(response.error?.message ?: "Login Google gagal.")
        tokenStorage.saveSession(
            accessToken = data.accessToken,
            refreshToken = data.refreshToken,
            userId = data.user.id,
            isPro = data.user.isPro,
        )
        data
    }

    suspend fun loginAsGuest(): Result<AuthResponseDto> = runCatching {
        val response = authApi.loginAsGuest(GuestLoginRequest())
        val data = response.data ?: error(response.error?.message ?: "Login Guest gagal.")
        tokenStorage.saveSession(
            accessToken = data.accessToken,
            refreshToken = data.refreshToken,
            userId = data.user.id,
            isPro = data.user.isPro,
        )
        data
    }

    suspend fun logout() {
        tokenStorage.clearSession()
    }
}
