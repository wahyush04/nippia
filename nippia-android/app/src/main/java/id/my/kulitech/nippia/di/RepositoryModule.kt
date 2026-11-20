package id.my.kulitech.nippia.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.my.kulitech.nippia.data.repository.AuthRepositoryImpl
import id.my.kulitech.nippia.data.repository.LearningPathRepositoryImpl
import id.my.kulitech.nippia.data.repository.OnboardingRepositoryImpl
import id.my.kulitech.nippia.domain.repository.AuthRepository
import id.my.kulitech.nippia.domain.repository.LearningPathRepository
import id.my.kulitech.nippia.domain.repository.OnboardingRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(impl: OnboardingRepositoryImpl): OnboardingRepository

    @Binds
    @Singleton
    abstract fun bindLearningPathRepository(impl: LearningPathRepositoryImpl): LearningPathRepository
}
