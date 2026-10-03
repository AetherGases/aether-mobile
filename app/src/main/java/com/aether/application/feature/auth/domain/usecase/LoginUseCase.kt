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
        val trimmedEmail = email.trim()
        // Empty fields
        if (trimmedEmail.isBlank())
            return Result.failure(IllegalArgumentException("É necessário informar um e-mail."))

        if (password.isBlank())
            return Result.failure(IllegalArgumentException("A senha é obrigatória."))

        return authRepository.login(trimmedEmail, password)
    }
}
