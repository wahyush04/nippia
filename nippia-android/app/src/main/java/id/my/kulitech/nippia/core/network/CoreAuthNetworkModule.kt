package id.my.kulitech.nippia.core.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.my.kulitech.nippia.core.network.auth.AuthApiService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreAuthNetworkModule {

    @Provides
    @Singleton
    fun provideCoreAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)
}
