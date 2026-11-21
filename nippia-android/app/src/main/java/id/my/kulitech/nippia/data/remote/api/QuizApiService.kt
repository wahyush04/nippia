package id.my.kulitech.nippia.data.remote.api

import id.my.kulitech.nippia.data.remote.dto.ApiResponse
import id.my.kulitech.nippia.data.remote.dto.CompleteUnitDataDto
import id.my.kulitech.nippia.data.remote.dto.CompleteUnitRequestDto
import id.my.kulitech.nippia.data.remote.dto.UnitQuestionsDataDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface QuizApiService {

    @GET("units/{unitId}/questions")
    suspend fun getUnitQuestions(
        @Path("unitId") unitId: String
    ): ApiResponse<UnitQuestionsDataDto>

    @POST("units/{unitId}/complete")
    suspend fun completeUnit(
        @Path("unitId") unitId: String,
        @Header("Idempotency-Key") idempotencyKey: String,
        @Body request: CompleteUnitRequestDto
    ): ApiResponse<CompleteUnitDataDto>
}
