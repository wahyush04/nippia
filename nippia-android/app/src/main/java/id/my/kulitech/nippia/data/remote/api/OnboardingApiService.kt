package id.my.kulitech.nippia.data.remote.api

import id.my.kulitech.nippia.data.remote.dto.ApiResponse
import id.my.kulitech.nippia.data.remote.dto.DiagnosticQuestionsDataDto
import id.my.kulitech.nippia.data.remote.dto.FormationsDataDto
import id.my.kulitech.nippia.data.remote.dto.SubmitOnboardingDataDto
import id.my.kulitech.nippia.data.remote.dto.SubmitOnboardingRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface OnboardingApiService {

    @GET("onboarding/formations")
    suspend fun getFormations(): ApiResponse<FormationsDataDto>

    @GET("onboarding/diagnostic-questions")
    suspend fun getDiagnosticQuestions(): ApiResponse<DiagnosticQuestionsDataDto>

    @PATCH("users/me/onboarding")
    suspend fun submitOnboarding(
        @Body request: SubmitOnboardingRequest
    ): ApiResponse<SubmitOnboardingDataDto>
}
