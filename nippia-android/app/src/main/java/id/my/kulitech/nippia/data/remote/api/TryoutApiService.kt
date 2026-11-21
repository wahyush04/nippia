package id.my.kulitech.nippia.data.remote.api

import id.my.kulitech.nippia.data.remote.dto.ApiResponse
import id.my.kulitech.nippia.data.remote.dto.StartDrillRequestDto
import id.my.kulitech.nippia.data.remote.dto.StartTryoutDataDto
import id.my.kulitech.nippia.data.remote.dto.SubmitTryoutDataDto
import id.my.kulitech.nippia.data.remote.dto.SubmitTryoutRequestDto
import id.my.kulitech.nippia.data.remote.dto.TryoutCatalogDataDto
import id.my.kulitech.nippia.data.remote.dto.TryoutLobbyDataDto
import id.my.kulitech.nippia.data.remote.dto.TryoutReviewDataDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TryoutApiService {

    @GET("tryouts")
    suspend fun getTryouts(): ApiResponse<TryoutCatalogDataDto>

    @GET("tryouts/{packageId}/lobby")
    suspend fun getTryoutLobby(
        @Path("packageId") packageId: String
    ): ApiResponse<TryoutLobbyDataDto>

    @POST("tryouts/drill/start")
    suspend fun startCategoryDrill(
        @Body request: StartDrillRequestDto
    ): ApiResponse<StartTryoutDataDto>

    @POST("tryouts/{packageId}/start")
    suspend fun startTryout(
        @Path("packageId") packageId: String
    ): ApiResponse<StartTryoutDataDto>

    @POST("tryouts/attempts/{attemptId}/submit")
    suspend fun submitTryout(
        @Path("attemptId") attemptId: String,
        @Body request: SubmitTryoutRequestDto
    ): ApiResponse<SubmitTryoutDataDto>

    @GET("tryouts/attempts/{attemptId}/review")
    suspend fun getAttemptReview(
        @Path("attemptId") attemptId: String
    ): ApiResponse<TryoutReviewDataDto>
}
