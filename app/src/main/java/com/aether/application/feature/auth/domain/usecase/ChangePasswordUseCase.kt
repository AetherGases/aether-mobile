package com.aether.application.feature.auth.domain.usecase

import com.aether.application.feature.auth.domain.model.DefaultPasswordRules
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
        if (DefaultPasswordRules.any { !it.isSatisfiedBy(password) })
            return Result.failure(IllegalArgumentException("A senha não atende aos requisitos!"))

        if (password != confirmPassword)
            return Result.failure(IllegalArgumentException("As senhas não coincidem!"))

        return authRepository.changePassword(email, password, key)
    }
}
