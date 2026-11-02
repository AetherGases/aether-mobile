package com.aether.application.feature.auth.domain.usecase

import com.aether.application.R
import com.aether.application.core.auth.model.Session
import com.aether.application.core.utils.UserFacingException
import com.aether.application.core.utils.uiText
import com.aether.application.feature.auth.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        rememberMe: Boolean
    ): Result<Session> {
        val trimmedEmail = email.trim()
        // Empty fields
        if (trimmedEmail.isBlank())
            return Result.failure(UserFacingException(uiText(R.string.auth_error_email_required)))

        if (password.isBlank())
            return Result.failure(UserFacingException(uiText(R.string.auth_error_password_required)))

        return authRepository.login(trimmedEmail, password, rememberMe)
    }
}
