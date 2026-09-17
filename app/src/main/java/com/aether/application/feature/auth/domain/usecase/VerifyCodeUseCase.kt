package com.aether.application.feature.auth.domain.usecase

import com.aether.application.feature.auth.data.remote.dto.ResetPasswordValidateCodeResponse
import com.aether.application.feature.auth.domain.repository.AuthRepository

class VerifyCodeUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        code: String
    ): Result<ResetPasswordValidateCodeResponse> {
        return authRepository.validateRecoveryCode(email, code)
    }
}
