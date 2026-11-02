package com.aether.application.feature.auth.domain.usecase

import com.aether.application.R
import com.aether.application.core.utils.UserFacingException
import com.aether.application.core.utils.uiText
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
            return Result.failure(UserFacingException(uiText(R.string.auth_error_password_requirements)))

        if (password != confirmPassword)
            return Result.failure(UserFacingException(uiText(R.string.auth_error_passwords_mismatch)))

        return authRepository.changePassword(email, password, key)
    }
}
