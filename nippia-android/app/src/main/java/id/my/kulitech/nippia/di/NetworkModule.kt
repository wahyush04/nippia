package id.my.kulitech.nippia.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.my.kulitech.nippia.BuildConfig
import id.my.kulitech.nippia.data.remote.api.AuthApiService
import id.my.kulitech.nippia.data.remote.api.ContentSyncApiService
import id.my.kulitech.nippia.data.remote.api.LeaderboardApiService
import id.my.kulitech.nippia.data.remote.api.LearningGamificationApiService
import id.my.kulitech.nippia.data.remote.api.LearningPathApiService
import id.my.kulitech.nippia.data.remote.api.OnboardingApiService
import id.my.kulitech.nippia.data.remote.api.ProfileApiService
import id.my.kulitech.nippia.data.remote.api.QuizApiService
import id.my.kulitech.nippia.data.remote.api.TryoutApiService
import id.my.kulitech.nippia.data.remote.interceptor.AuthInterceptor
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val TIMEOUT_SECONDS = 30L

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Provides
    @Singleton
    fun provideChuckerInterceptor(
        @ApplicationContext context: Context
    ): ChuckerInterceptor = ChuckerInterceptor.Builder(context).build()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(chuckerInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideOnboardingApiService(retrofit: Retrofit): OnboardingApiService =
        retrofit.create(OnboardingApiService::class.java)

    @Provides
    @Singleton
    fun provideLearningPathApiService(retrofit: Retrofit): LearningPathApiService =
        retrofit.create(LearningPathApiService::class.java)

    @Provides
    @Singleton
    fun provideTryoutApiService(retrofit: Retrofit): TryoutApiService =
        retrofit.create(TryoutApiService::class.java)

    @Provides
    @Singleton
    fun provideLeaderboardApiService(retrofit: Retrofit): LeaderboardApiService =
        retrofit.create(LeaderboardApiService::class.java)

    @Provides
    @Singleton
    fun provideProfileApiService(retrofit: Retrofit): ProfileApiService =
        retrofit.create(ProfileApiService::class.java)

    @Provides
    @Singleton
    fun provideQuizApiService(retrofit: Retrofit): QuizApiService =
        retrofit.create(QuizApiService::class.java)

    @Provides
    @Singleton
    fun provideContentSyncApiService(retrofit: Retrofit): ContentSyncApiService =
        retrofit.create(ContentSyncApiService::class.java)

    @Provides
    @Singleton
    fun provideLearningGamificationApiService(retrofit: Retrofit): LearningGamificationApiService =
        retrofit.create(LearningGamificationApiService::class.java)
}
