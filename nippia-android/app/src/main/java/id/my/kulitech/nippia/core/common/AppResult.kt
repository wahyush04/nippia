package id.my.kulitech.nippia.core.common

/**
 * A generic wrapper representing the outcome of a domain or data operation.
 */
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(
        val message: String,
        val code: String? = null,
        val throwable: Throwable? = null
    ) : AppResult<Nothing>
}
