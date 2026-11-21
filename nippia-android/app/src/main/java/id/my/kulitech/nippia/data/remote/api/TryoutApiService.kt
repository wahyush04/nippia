package id.my.kulitech.nippia.data.remote.api

import id.my.kulitech.nippia.data.remote.dto.ApiResponse
import id.my.kulitech.nippia.data.remote.dto.StartTryoutDataDto
import id.my.kulitech.nippia.data.remote.dto.SubmitTryoutDataDto
import id.my.kulitech.nippia.data.remote.dto.SubmitTryoutRequestDto
import id.my.kulitech.nippia.data.remote.dto.TryoutCatalogDataDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TryoutApiService {

    @GET("tryouts")
    suspend fun getTryouts(): ApiResponse<TryoutCatalogDataDto>

    @POST("tryouts/{packageId}/start")
    suspend fun startTryout(
        @Path("packageId") packageId: String
    ): ApiResponse<StartTryoutDataDto>

    @POST("tryouts/attempts/{attemptId}/submit")
    suspend fun submitTryout(
        @Path("attemptId") attemptId: String,
        @Body request: SubmitTryoutRequestDto
    ): ApiResponse<SubmitTryoutDataDto>
}
