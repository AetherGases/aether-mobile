package com.aether.application.feature.auth.data.repository

import android.util.Log
import com.aether.application.core.auth.model.Session
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.auth.data.remote.AuthApi
import com.aether.application.feature.auth.data.remote.ProfileApi
import com.aether.application.feature.auth.data.remote.dto.ErrorResponse
import com.aether.application.feature.auth.data.remote.dto.LoginRequest
import com.aether.application.feature.auth.data.remote.dto.ResetPasswordChangePasswordRequest
import com.aether.application.feature.auth.data.remote.dto.ResetPasswordResendCodeRequest
import com.aether.application.feature.auth.data.remote.dto.ResetPasswordSendCodeRequest
import com.aether.application.feature.auth.data.remote.dto.ResetPasswordValidateCodeRequest
import com.aether.application.feature.auth.domain.exception.AuthException
import com.aether.application.feature.auth.domain.repository.AuthRepository
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection.HTTP_FORBIDDEN

private val errorJson = Json { ignoreUnknownKeys = true }

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val profileApi: ProfileApi,
    private val sessionManager: SessionManager
): AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<Session> = runCatchingAuth {
        val response = api.login(
            LoginRequest(email, password)
        )

        var session = response.toDomain()
        if (!sessionManager.save(session)) {
            return Result.failure(AuthException.Unexpected(IllegalStateException("Failed to persist session")))
        }

        runCatchingAuth {
            val profile = profileApi.getUserProfile()
            val sessionWithPermissions = session.copy(permissions = profile.permissions)
            if (sessionManager.save(sessionWithPermissions)) {
                session = sessionWithPermissions
            } else {
                return Result.failure(AuthException.Unexpected(IllegalStateException("Failed to persist permissions")))
            }
        }

        session
    }

    override suspend fun sendRecoveryPassword(
        email: String
    ): Result<Unit> = runCatchingAuth {
        api.resetPasswordSendCode(ResetPasswordSendCodeRequest(email))
    }

    override suspend fun resendRecoveryCode(
        email: String
    ): Result<Unit> = runCatchingAuth {
        api.resetPasswordResendCode(ResetPasswordResendCodeRequest(email))
    }

    override suspend fun validateRecoveryCode(
        email: String,
        code: String
    ): Result<String> = runCatchingAuth {
        api.resetPasswordValidateCode(
            ResetPasswordValidateCodeRequest(email = email, code = code)
        ).toDomain()
    }

    override suspend fun changePassword(
        email: String,
        password: String,
        key: String
    ): Result<Unit> = runCatchingAuth {
        api.resetPasswordChangePassword(
            ResetPasswordChangePasswordRequest(email = email, password = password, key = key)
        )
    }

    private inline fun <T> runCatchingAuth(block: () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", e.message ?: "unexpected error", e)
            Result.failure(e.toAuthException())
        }

    private fun Exception.toAuthException(): AuthException = when (this) {
        is AuthException -> this
        is HttpException -> toHttpAuthException()
        is IOException -> AuthException.Network(this)
        else -> AuthException.Unexpected(this)
    }

    private fun HttpException.toHttpAuthException(): AuthException {
        if (code() == HTTP_FORBIDDEN) return AuthException.InvalidCredentials()

        val message = readErrorMessage()
        if (message.isNullOrBlank()) return AuthException.Unexpected(this)

        return AuthException.Api(message)
    }

    private fun HttpException.readErrorMessage(): String? {
        val body = response()?.errorBody()?.string() ?: return null

        return try {
            errorJson.decodeFromString<ErrorResponse>(body).message
        } catch (_: SerializationException) {
            null
        }
    }
}
