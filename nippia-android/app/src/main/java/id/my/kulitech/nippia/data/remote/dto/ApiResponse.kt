package id.my.kulitech.nippia.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Standard REST API response envelope matching docs/API_CONTRACT.md.
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val meta: ApiMeta? = null,
    val error: ApiError? = null
)

@Serializable
data class ApiMeta(
    val serverTimestamp: String? = null
)

@Serializable
data class ApiError(
    val code: String,
    val message: String,
    val details: List<ApiErrorDetail> = emptyList()
)

@Serializable
data class ApiErrorDetail(
    val field: String,
    val issue: String
)
