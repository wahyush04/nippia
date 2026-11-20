package id.my.kulitech.nippia.core.network.auth

import id.my.kulitech.nippia.core.network.ApiEnvelope
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/google")
    suspend fun loginWithGoogle(@Body body: GoogleLoginRequest): ApiEnvelope<AuthResponseDto>

    @POST("auth/guest")
    suspend fun loginAsGuest(@Body body: GuestLoginRequest = GuestLoginRequest()): ApiEnvelope<AuthResponseDto>

    @POST("auth/link-google")
    suspend fun linkGoogle(@Body body: GoogleLoginRequest): ApiEnvelope<AuthResponseDto>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body body: RefreshTokenRequest): ApiEnvelope<RefreshResponseDto>
}
