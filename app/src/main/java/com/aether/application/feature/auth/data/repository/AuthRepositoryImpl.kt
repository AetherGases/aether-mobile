package com.aether.application.feature.auth.data.repository

import android.util.Log
import com.aether.application.core.auth.model.Session
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.auth.data.remote.AuthApi
import com.aether.application.feature.auth.data.remote.ProfileApi
import com.aether.application.feature.auth.data.remote.dto.LoginRequest
import com.aether.application.feature.auth.domain.exception.AuthException
import com.aether.application.feature.auth.domain.repository.AuthRepository
import kotlin.coroutines.cancellation.CancellationException

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val profileApi: ProfileApi,
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

            var session = response.toDomain()
            if (!sessionManager.save(session)) {
                return Result.failure(AuthException.Unexpected(IllegalStateException("Failed to persist session")))
            }

            try {
                val profile = profileApi.getUserProfile()
                val sessionWithPermissions = session.copy(permissions = profile.permissions)
                if (sessionManager.save(sessionWithPermissions)) {
                    session = sessionWithPermissions
                } else {
                    return Result.failure(AuthException.Unexpected(IllegalStateException("Failed to persist permissions")))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("AuthRepositoryImpl", "Failed to fetch permissions: ${e.message}")
            }

            Result.success(session)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", e.message ?: "unexpected error")
            Result.failure(
                e as? AuthException ?: AuthException.Unexpected(e)
            )
        }
    }

}
