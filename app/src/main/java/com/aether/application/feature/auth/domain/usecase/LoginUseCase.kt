package com.aether.application.feature.auth.domain.usecase

import com.aether.application.core.auth.model.Session
import com.aether.application.feature.auth.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<Session> {
        // Empty fields
        if (email.isBlank())
            return Result.failure(IllegalArgumentException("Email não pode estar vazio!"))

        if (password.isBlank())
            return Result.failure(IllegalArgumentException("Senha não pode estar vazia!"))

        return authRepository.login(email, password)
    }
}
