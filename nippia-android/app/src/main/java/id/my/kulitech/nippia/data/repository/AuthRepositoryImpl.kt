package id.my.kulitech.nippia.data.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.core.datastore.SessionPreferences
import id.my.kulitech.nippia.data.remote.api.AuthApiService
import id.my.kulitech.nippia.data.remote.dto.ApiResponse
import id.my.kulitech.nippia.data.remote.dto.AuthDataDto
import id.my.kulitech.nippia.data.remote.dto.GoogleAuthRequest
import id.my.kulitech.nippia.data.remote.dto.GuestAuthRequest
import id.my.kulitech.nippia.data.remote.dto.LinkGoogleRequest
import id.my.kulitech.nippia.data.remote.dto.RefreshTokenRequest
import id.my.kulitech.nippia.domain.model.AuthSession
import id.my.kulitech.nippia.domain.model.SessionState
import id.my.kulitech.nippia.domain.repository.AuthRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val sessionPreferences: SessionPreferences,
    private val json: Json
) : AuthRepository {

    override suspend fun checkSession(): SessionState {
        val accessToken = sessionPreferences.accessToken.firstOrNull()
        val refreshToken = sessionPreferences.refreshToken.firstOrNull()

        if (accessToken.isNullOrBlank()) {
            return SessionState.Unauthenticated
        }

        return try {
            val response = authApiService.getMyProfile()
            val profile = response.data ?: return SessionState.Unauthenticated
            sessionPreferences.setProStatus(profile.user.isPro)
            SessionState.Authenticated(
                user = profile.user.toDomain(),
                gamification = profile.gamification?.toDomain()
            )
        } catch (e: HttpException) {
            if (e.code() == 401 && !refreshToken.isNullOrBlank()) {
                tryRefreshAndFetchProfile(refreshToken)
            } else {
                sessionPreferences.clearSession()
                SessionState.Unauthenticated
            }
        } catch (_: Exception) {
            // Network unavailable during cold start; treat as unauthenticated unless offline cache exists
            SessionState.Unauthenticated
        }
    }

    private suspend fun tryRefreshAndFetchProfile(refreshToken: String): SessionState {
        return try {
            val refreshResponse = authApiService.refreshToken(RefreshTokenRequest(refreshToken))
            val tokens = refreshResponse.data ?: run {
                sessionPreferences.clearSession()
                return SessionState.Unauthenticated
            }
            sessionPreferences.saveTokens(tokens.accessToken, tokens.refreshToken)

            val profile = authApiService.getMyProfile().data ?: return SessionState.Unauthenticated
            sessionPreferences.setProStatus(profile.user.isPro)
            SessionState.Authenticated(
                user = profile.user.toDomain(),
                gamification = profile.gamification?.toDomain()
            )
        } catch (_: Exception) {
            sessionPreferences.clearSession()
            SessionState.Unauthenticated
        }
    }

    override suspend fun loginWithGoogle(
        idToken: String,
        fcmToken: String?
    ): AppResult<AuthSession> = executeAuthCall {
        authApiService.loginWithGoogle(GoogleAuthRequest(idToken = idToken, fcmToken = fcmToken))
    }

    override suspend fun loginAsGuest(fcmToken: String?): AppResult<AuthSession> =
        executeAuthCall {
            authApiService.loginAsGuest(GuestAuthRequest(fcmToken = fcmToken))
        }

    override suspend fun linkGoogleAccount(idToken: String): AppResult<AuthSession> =
        executeAuthCall {
            authApiService.linkGoogleAccount(LinkGoogleRequest(idToken = idToken))
        }

    override suspend fun logout() {
        sessionPreferences.clearSession()
    }

    private suspend fun executeAuthCall(
        call: suspend () -> ApiResponse<AuthDataDto>
    ): AppResult<AuthSession> {
        return try {
            val response = call()
            val data = response.data
            if (response.success && data != null) {
                sessionPreferences.saveTokens(data.accessToken, data.refreshToken)
                sessionPreferences.setProStatus(data.user.isPro)
                AppResult.Success(data.toDomain())
            } else {
                AppResult.Error(
                    message = response.error?.message ?: "Gagal melakukan autentikasi.",
                    code = response.error?.code
                )
            }
        } catch (e: HttpException) {
            val parsedError = parseHttpError(e)
            AppResult.Error(
                message = parsedError?.message ?: "Terjadi kesalahan server (${e.code()}).",
                code = parsedError?.code,
                throwable = e
            )
        } catch (e: Exception) {
            AppResult.Error(
                message = e.localizedMessage ?: "Tidak dapat terhubung ke server. Periksa koneksi internet Anda.",
                throwable = e
            )
        }
    }

    private fun parseHttpError(exception: HttpException) = try {
        val rawBody = exception.response()?.errorBody()?.string()
        if (rawBody.isNullOrBlank()) null
        else json.decodeFromString<ApiResponse<Unit>>(rawBody).error
    } catch (_: Exception) {
        null
    }
}
