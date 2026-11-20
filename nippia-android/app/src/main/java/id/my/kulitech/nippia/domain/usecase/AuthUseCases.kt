package id.my.kulitech.nippia.domain.usecase

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.AuthSession
import id.my.kulitech.nippia.domain.model.SessionState
import id.my.kulitech.nippia.domain.repository.AuthRepository
import javax.inject.Inject

class CheckSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): SessionState = authRepository.checkSession()
}

class LoginWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String, fcmToken: String? = null): AppResult<AuthSession> =
        authRepository.loginWithGoogle(idToken = idToken, fcmToken = fcmToken)
}

class LoginAsGuestUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(fcmToken: String? = null): AppResult<AuthSession> =
        authRepository.loginAsGuest(fcmToken = fcmToken)
}
