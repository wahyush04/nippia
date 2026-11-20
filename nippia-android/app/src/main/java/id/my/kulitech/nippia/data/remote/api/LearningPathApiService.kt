package id.my.kulitech.nippia.data.remote.api

import id.my.kulitech.nippia.data.remote.dto.ApiResponse
import id.my.kulitech.nippia.data.remote.dto.LearningPathDataDto
import retrofit2.http.GET
import retrofit2.http.Query

interface LearningPathApiService {

    @GET("learning-path")
    suspend fun getLearningPath(
        @Query("category") category: String? = null
    ): ApiResponse<LearningPathDataDto>
}
