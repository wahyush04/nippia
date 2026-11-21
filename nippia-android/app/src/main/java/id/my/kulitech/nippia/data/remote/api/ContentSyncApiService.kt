package id.my.kulitech.nippia.data.remote.api

import id.my.kulitech.nippia.data.remote.dto.ApiResponse
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

@Serializable
data class DeltaSyncDataDto(
    val syncTimestamp: String = ""
)

interface ContentSyncApiService {
    @GET("content/sync")
    suspend fun deltaSync(
        @Query("since") since: String? = null
    ): ApiResponse<DeltaSyncDataDto>
}
