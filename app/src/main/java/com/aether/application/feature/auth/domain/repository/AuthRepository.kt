package com.aether.application.feature.auth.domain.repository

import com.aether.application.core.auth.model.Session
import com.aether.application.feature.auth.data.remote.dto.ResetPasswordValidateCodeResponse

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Result<Session>

    suspend fun sendRecoveryPassword(
        email: String,
    ): Result<Unit>

    suspend fun validateRecoveryCode(
        email: String,
        code: String
    ): Result<ResetPasswordValidateCodeResponse>

    suspend fun changePassword(
        email: String,
        password: String,
        key: String
    ): Result<Unit>
}
