package com.aether.application.feature.auth.domain.usecase

import com.aether.application.core.auth.model.Session
import com.aether.application.feature.auth.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Session> {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password must not be blank"))
        }

        return authRepository.login(trimmedEmail, password)
    }
}
