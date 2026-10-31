package com.aether.application.feature.auth.domain.usecase

import com.aether.application.R
import com.aether.application.core.utils.UserFacingException
import com.aether.application.core.utils.uiText
import com.aether.application.feature.auth.domain.repository.AuthRepository

class RequestPasswordRecoveryUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String
    ): Result<Unit> {
        val trimmedEmail = email.trim()
        val emailRegex = Regex("^[A-Za-z0-9.!#\$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+$")

        if (!emailRegex.matches(trimmedEmail))
            return Result.failure(UserFacingException(uiText(R.string.auth_error_email_invalid)))

        return authRepository.sendRecoveryPassword(trimmedEmail)
    }
}