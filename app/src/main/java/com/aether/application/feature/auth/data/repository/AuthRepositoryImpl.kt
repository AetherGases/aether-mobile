package com.aether.application.feature.auth.data.repository

import android.util.Log
import com.aether.application.core.auth.model.Session
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.auth.data.remote.AuthApi
import com.aether.application.feature.auth.data.remote.dto.LoginRequest
import com.aether.application.feature.auth.domain.exception.AuthException
import com.aether.application.feature.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val sessionManager: SessionManager
): AuthRepository {
    override suspend fun login(
        email: String,
        password: String
    ): Result<Session> {
        return try {
            val response = api.login(
                LoginRequest(email, password)
            )

            val session = response.toDomain()

            sessionManager.save(session)

            Result.success(session)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", e.message ?: "unexpected error")
            Result.failure(
                e as? AuthException ?: AuthException.Unexpected(e)
            )
        }
    }

}
