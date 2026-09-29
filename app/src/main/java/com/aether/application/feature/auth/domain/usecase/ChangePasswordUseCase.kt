package com.aether.application.feature.auth.domain.usecase

import com.aether.application.feature.auth.domain.model.PASSWORD_REQUIREMENTS_MESSAGE
import com.aether.application.feature.auth.domain.model.meetsPasswordRules
import com.aether.application.feature.auth.domain.repository.AuthRepository

class ChangePasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        key: String,
        password: String,
        confirmPassword: String
    ): Result<Unit> {
        if (!password.meetsPasswordRules())
            return Result.failure(IllegalArgumentException(PASSWORD_REQUIREMENTS_MESSAGE))

        if (password != confirmPassword)
            return Result.failure(IllegalArgumentException("As senhas não coincidem."))

        return authRepository.changePassword(email, password, key)
    }
}
