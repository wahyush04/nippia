package id.my.kulitech.nippia.data.remote.api

import id.my.kulitech.nippia.data.remote.dto.ApiResponse
import id.my.kulitech.nippia.data.remote.dto.AuthDataDto
import id.my.kulitech.nippia.data.remote.dto.GoogleAuthRequest
import id.my.kulitech.nippia.data.remote.dto.GuestAuthRequest
import id.my.kulitech.nippia.data.remote.dto.LinkGoogleRequest
import id.my.kulitech.nippia.data.remote.dto.RefreshDataDto
import id.my.kulitech.nippia.data.remote.dto.RefreshTokenRequest
import id.my.kulitech.nippia.data.remote.dto.UserProfileDataDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/google")
    suspend fun loginWithGoogle(
        @Body request: GoogleAuthRequest
    ): ApiResponse<AuthDataDto>

    @POST("auth/guest")
    suspend fun loginAsGuest(
        @Body request: GuestAuthRequest
    ): ApiResponse<AuthDataDto>

    @POST("auth/link-google")
    suspend fun linkGoogleAccount(
        @Body request: LinkGoogleRequest
    ): ApiResponse<AuthDataDto>

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): ApiResponse<RefreshDataDto>

    @GET("users/me")
    suspend fun getMyProfile(): ApiResponse<UserProfileDataDto>
}
