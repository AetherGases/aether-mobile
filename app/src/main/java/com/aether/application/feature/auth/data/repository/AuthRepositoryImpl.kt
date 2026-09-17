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

    override suspend fun sendRecoveryPassword(email: String): Result<Unit> {
        return try {
            api.resetPasswordSendCode(
                ResetPasswordSendCodeRequest(email)
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", e.message ?: "unexpected error")
            Result.failure(
                e as? AuthException ?: AuthException.Unexpected(e)
            )
        }
    }

    override suspend fun validateRecoveryCode(
        email: String,
        code: String
    ): Result<ResetPasswordValidateCodeResponse> {
        return try {
            val response = api.resetPasswordValidateCode(
                ResetPasswordValidateCodeRequest(
                    email = email,
                    code = code
                )
            )

            Result.success(ResetPasswordValidateCodeResponse(key = response.key))
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", e.message ?: "unexpected error")
            Result.failure(
                e as? AuthException ?: AuthException.Unexpected(e)
            )
        }
    }

    override suspend fun changePassword(
        email: String,
        password: String,
        key: String
    ): Result<Unit> {
        return try {
            api.resetPasswordChangePassword(
                ResetPasswordChangePasswordRequest(
                    email = email,
                    password = password,
                    key = key
                )
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", e.message ?: "unexpected error")
            Result.failure(
                e as? AuthException ?: AuthException.Unexpected(e)
            )
        }
    }

}
