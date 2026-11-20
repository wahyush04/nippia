package id.my.kulitech.nippia.core.network

import kotlinx.serialization.Serializable

@Serializable
data class ApiEnvelope<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiErrorDto? = null,
    val meta: ApiMetaDto? = null,
)

@Serializable
data class ApiErrorDto(
    val code: String,
    val message: String,
)

@Serializable
data class ApiMetaDto(
    val serverTimestamp: String,
)
