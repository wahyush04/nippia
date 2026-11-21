package id.my.kulitech.nippia.data.remote.api

import id.my.kulitech.nippia.data.remote.dto.ApiResponse
import id.my.kulitech.nippia.data.remote.dto.ClearMistakesDataDto
import id.my.kulitech.nippia.data.remote.dto.GamificationDetailDto
import id.my.kulitech.nippia.data.remote.dto.GamificationMethodRequestDto
import id.my.kulitech.nippia.data.remote.dto.HeartRefillDataDto
import id.my.kulitech.nippia.data.remote.dto.MistakeAttemptDataDto
import id.my.kulitech.nippia.data.remote.dto.MistakeAttemptRequestDto
import id.my.kulitech.nippia.data.remote.dto.MistakeBankDataDto
import id.my.kulitech.nippia.data.remote.dto.StreakFreezeDataDto
import id.my.kulitech.nippia.data.remote.dto.UnitStudyGuideDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LearningGamificationApiService {

    @GET("units/{unitId}/guide")
    suspend fun getUnitStudyGuide(
        @Path("unitId") unitId: String
    ): ApiResponse<UnitStudyGuideDto>

    @GET("learning/mistakes")
    suspend fun getMistakeBank(
        @Query("categoryCode") categoryCode: String? = null,
        @Query("unresolvedOnly") unresolvedOnly: Boolean = true
    ): ApiResponse<MistakeBankDataDto>

    @POST("learning/mistakes/{questionId}/attempt")
    suspend fun attemptMistake(
        @Path("questionId") questionId: String,
        @Body body: MistakeAttemptRequestDto
    ): ApiResponse<MistakeAttemptDataDto>

    @DELETE("learning/mistakes/resolved")
    suspend fun clearResolvedMistakes(): ApiResponse<ClearMistakesDataDto>

    @GET("users/me/gamification")
    suspend fun getGamificationStatus(): ApiResponse<GamificationDetailDto>

    @POST("users/me/streak-freeze")
    suspend fun purchaseStreakFreeze(
        @Body body: GamificationMethodRequestDto
    ): ApiResponse<StreakFreezeDataDto>

    @POST("users/me/hearts/refill")
    suspend fun refillHearts(
        @Body body: GamificationMethodRequestDto
    ): ApiResponse<HeartRefillDataDto>
}
