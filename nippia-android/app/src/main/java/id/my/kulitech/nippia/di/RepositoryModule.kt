package id.my.kulitech.nippia.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.my.kulitech.nippia.data.repository.AuthRepositoryImpl
import id.my.kulitech.nippia.data.repository.LeaderboardRepositoryImpl
import id.my.kulitech.nippia.data.repository.LearningPathRepositoryImpl
import id.my.kulitech.nippia.data.repository.OnboardingRepositoryImpl
import id.my.kulitech.nippia.data.repository.ProfileRepositoryImpl
import id.my.kulitech.nippia.data.repository.QuizRepositoryImpl
import id.my.kulitech.nippia.data.repository.TryoutRepositoryImpl
import id.my.kulitech.nippia.domain.repository.AuthRepository
import id.my.kulitech.nippia.domain.repository.LeaderboardRepository
import id.my.kulitech.nippia.domain.repository.LearningPathRepository
import id.my.kulitech.nippia.domain.repository.OnboardingRepository
import id.my.kulitech.nippia.domain.repository.ProfileRepository
import id.my.kulitech.nippia.domain.repository.QuizRepository
import id.my.kulitech.nippia.domain.repository.TryoutRepository
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

    @Binds
    @Singleton
    abstract fun bindTryoutRepository(impl: TryoutRepositoryImpl): TryoutRepository

    @Binds
    @Singleton
    abstract fun bindLeaderboardRepository(impl: LeaderboardRepositoryImpl): LeaderboardRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindQuizRepository(impl: QuizRepositoryImpl): QuizRepository
}
