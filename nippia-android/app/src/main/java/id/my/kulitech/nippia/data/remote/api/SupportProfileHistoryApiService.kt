package id.my.kulitech.nippia.data.remote.api

import id.my.kulitech.nippia.data.remote.dto.ApiResponse
import id.my.kulitech.nippia.data.remote.dto.BookmarkItemDto
import id.my.kulitech.nippia.data.remote.dto.BookmarkOverviewDataDto
import id.my.kulitech.nippia.data.remote.dto.DeleteAccountDataDto
import id.my.kulitech.nippia.data.remote.dto.DeleteAccountRequestDto
import id.my.kulitech.nippia.data.remote.dto.FaqCatalogDataDto
import id.my.kulitech.nippia.data.remote.dto.ReportQuestionDataDto
import id.my.kulitech.nippia.data.remote.dto.ReportQuestionRequestDto
import id.my.kulitech.nippia.data.remote.dto.TryoutHistoryDataDto
import id.my.kulitech.nippia.data.remote.dto.UpdateProfileRequestDto
import id.my.kulitech.nippia.data.remote.dto.UpsertBookmarkRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SupportProfileHistoryApiService {

    @PATCH("users/me/profile")
    suspend fun updateProfile(
        @Body body: UpdateProfileRequestDto
    ): ApiResponse<Unit>

    @HTTP(method = "DELETE", path = "users/me", hasBody = true)
    suspend fun deleteAccount(
        @Body body: DeleteAccountRequestDto
    ): ApiResponse<DeleteAccountDataDto>

    @GET("tryouts/history")
    suspend fun getTryoutHistory(): ApiResponse<TryoutHistoryDataDto>

    @GET("learning/bookmarks")
    suspend fun listBookmarks(
        @Query("itemType") itemType: String? = null,
        @Query("categoryCode") categoryCode: String? = null
    ): ApiResponse<BookmarkOverviewDataDto>

    @POST("learning/bookmarks")
    suspend fun upsertBookmark(
        @Body body: UpsertBookmarkRequestDto
    ): ApiResponse<BookmarkItemDto>

    @DELETE("learning/bookmarks/{bookmarkId}")
    suspend fun deleteBookmark(
        @Path("bookmarkId") bookmarkId: String
    ): ApiResponse<DeleteAccountDataDto>

    @GET("learning/faq")
    suspend fun getFaqCatalog(): ApiResponse<FaqCatalogDataDto>

    @POST("learning/report-question")
    suspend fun submitQuestionReport(
        @Body body: ReportQuestionRequestDto
    ): ApiResponse<ReportQuestionDataDto>
}
