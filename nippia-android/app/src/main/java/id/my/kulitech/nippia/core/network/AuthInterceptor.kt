package id.my.kulitech.nippia.core.network

import id.my.kulitech.nippia.core.datastore.TokenStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val path = original.url.encodedPath

        // Public auth routes do not require Bearer token
        if (path.endsWith("/auth/google") ||
            path.endsWith("/auth/guest") ||
            path.endsWith("/auth/refresh") ||
            path.endsWith("/auth/logout")
        ) {
            return chain.proceed(original)
        }

        val token = runBlocking { tokenStorage.getAccessToken() }
        val request = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
        return chain.proceed(request)
    }
}
