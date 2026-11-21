package id.my.kulitech.nippia.data.remote.api

import id.my.kulitech.nippia.data.remote.dto.ApiResponse
import id.my.kulitech.nippia.data.remote.dto.TryoutCatalogDataDto
import retrofit2.http.GET

interface TryoutApiService {

    @GET("tryouts")
    suspend fun getTryouts(): ApiResponse<TryoutCatalogDataDto>
}
