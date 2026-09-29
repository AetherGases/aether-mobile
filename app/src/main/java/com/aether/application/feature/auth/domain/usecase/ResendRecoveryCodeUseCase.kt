package com.aether.application.feature.auth.domain.usecase

import com.aether.application.feature.auth.domain.repository.AuthRepository

class ResendRecoveryCodeUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String
    ): Result<Unit> {
        return authRepository.resendRecoveryCode(email)
    }
}
