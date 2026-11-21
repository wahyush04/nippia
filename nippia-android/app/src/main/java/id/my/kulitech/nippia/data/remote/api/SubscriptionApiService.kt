package id.my.kulitech.nippia.data.remote.api

import id.my.kulitech.nippia.data.remote.dto.ApiResponse
import id.my.kulitech.nippia.data.remote.dto.CancelSubscriptionDataDto
import id.my.kulitech.nippia.data.remote.dto.PauseSubscriptionDataDto
import id.my.kulitech.nippia.data.remote.dto.PauseSubscriptionRequestDto
import id.my.kulitech.nippia.data.remote.dto.RestoreSubscriptionDataDto
import id.my.kulitech.nippia.data.remote.dto.SubscriptionCatalogDataDto
import id.my.kulitech.nippia.data.remote.dto.SubscriptionHistoryDataDto
import id.my.kulitech.nippia.data.remote.dto.VerifiedSubscriptionReceiptDto
import id.my.kulitech.nippia.data.remote.dto.VerifySubscriptionRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SubscriptionApiService {

    @GET("subscriptions/plans")
    suspend fun getPlans(): ApiResponse<SubscriptionCatalogDataDto>

    @POST("subscriptions/verify")
    suspend fun verifyPurchase(
        @Body body: VerifySubscriptionRequestDto
    ): ApiResponse<VerifiedSubscriptionReceiptDto>

    @POST("subscriptions/restore")
    suspend fun restorePurchases(): ApiResponse<RestoreSubscriptionDataDto>

    @GET("subscriptions/history")
    suspend fun getHistory(): ApiResponse<SubscriptionHistoryDataDto>

    @POST("subscriptions/pause")
    suspend fun pauseSubscription(
        @Body body: PauseSubscriptionRequestDto
    ): ApiResponse<PauseSubscriptionDataDto>

    @POST("subscriptions/cancel")
    suspend fun cancelSubscription(): ApiResponse<CancelSubscriptionDataDto>
}
