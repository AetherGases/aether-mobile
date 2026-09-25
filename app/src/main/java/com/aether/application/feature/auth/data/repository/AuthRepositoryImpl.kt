package com.aether.application.feature.auth.data.repository

import android.util.Log
import com.aether.application.core.auth.model.Session
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.auth.data.remote.AuthApi
import com.aether.application.feature.auth.data.remote.dto.LoginRequest
import com.aether.application.feature.auth.data.remote.dto.ResetPasswordChangePasswordRequest
import com.aether.application.feature.auth.data.remote.dto.ResetPasswordSendCodeRequest
import com.aether.application.feature.auth.data.remote.dto.ResetPasswordValidateCodeRequest
import com.aether.application.feature.auth.data.remote.dto.ResetPasswordValidateCodeResponse
import com.aether.application.feature.auth.domain.exception.AuthException
import com.aether.application.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val sessionManager: SessionManager
): AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<Session> = runCatchingAuth {
        api.login(LoginRequest(email, password))
            .toDomain()
            .also { sessionManager.save(it) }
    }

    override suspend fun sendRecoveryPassword(
        email: String
    ): Result<Unit> = runCatchingAuth {
        api.resetPasswordSendCode(ResetPasswordSendCodeRequest(email))
    }

    override suspend fun validateRecoveryCode(
        email: String,
        code: String
    ): Result<ResetPasswordValidateCodeResponse> = runCatchingAuth {
        api.resetPasswordValidateCode(
            ResetPasswordValidateCodeRequest(email = email, code = code)
        )
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", e.message ?: "unexpected error", e)
            Result.failure(e.toAuthException())
        }

    private fun Exception.toAuthException(): AuthException = when (this) {
        is AuthException -> this
        is HttpException -> when (code()) {
            401, 403, 404 -> AuthException.InvalidCredentials()
            else -> AuthException.Unexpected(this)
        }
        is IOException -> AuthException.Network(this)
        else -> AuthException.Unexpected(this)
    }

}
