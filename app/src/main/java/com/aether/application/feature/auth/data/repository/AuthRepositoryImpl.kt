package com.aether.application.feature.auth.data.repository

import com.aether.application.core.auth.model.Session
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.auth.data.remote.AuthAPI
import com.aether.application.feature.auth.data.remote.dto.LoginRequest
import com.aether.application.feature.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: AuthAPI,
    private val sessionManager: SessionManager
): AuthRepository {
    override suspend fun login(
        email: String,
        senha: String
    ): Result<Session> {
        return try {
            val response = api.login(
                LoginRequest(email, senha)
            )

            val session = response.toDomain()

            sessionManager.save(session)

            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}