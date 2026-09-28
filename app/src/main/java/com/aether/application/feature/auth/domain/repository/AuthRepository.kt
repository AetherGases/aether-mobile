package com.aether.application.feature.auth.domain.repository

import com.aether.application.core.auth.model.Session

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
    ): Result<String>

    suspend fun changePassword(
        email: String,
        password: String,
        key: String
    ): Result<Unit>
}
