package com.aether.application.feature.auth.data.repository

import android.util.Log
import com.aether.application.core.notification.domain.DeviceTokenRegistrar
import com.aether.application.core.auth.model.Session
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.core.database.ProfileLocalDataSource
import com.aether.application.core.network.readErrorMessage
import com.aether.application.feature.auth.data.remote.AuthApi
import com.aether.application.feature.auth.data.remote.ProfileApi
import com.aether.application.feature.auth.data.remote.dto.LoginRequest
import com.aether.application.feature.auth.data.remote.dto.ResetPasswordChangePasswordRequest
import com.aether.application.feature.auth.data.remote.dto.ResetPasswordResendCodeRequest
import com.aether.application.feature.auth.data.remote.dto.ResetPasswordSendCodeRequest
import com.aether.application.feature.auth.data.remote.dto.ResetPasswordValidateCodeRequest
import com.aether.application.feature.auth.domain.exception.AuthException
import com.aether.application.feature.auth.domain.repository.AuthRepository
import com.aether.application.feature.profile.data.remote.dto.NotificationPreferencesDto
import com.aether.application.feature.profile.data.remote.dto.UpdateProfileRequest
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection.HTTP_FORBIDDEN

private const val TAG = "AuthRepositoryImpl"

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val profileApi: ProfileApi,
    private val sessionManager: SessionManager,
    private val profileLocalDataSource: ProfileLocalDataSource,
    private val deviceTokenRegistrar: DeviceTokenRegistrar
): AuthRepository {

    override suspend fun login(
        email: String,
        password: String,
        rememberMe: Boolean
    ): Result<Session> = runCatchingAuth {
        val response = api.login(
            LoginRequest(email, password)
        )

        var session = response.toDomain()
        if (!sessionManager.save(session, remember = rememberMe)) {
            return Result.failure(AuthException.Unexpected(IllegalStateException("Failed to persist session")))
        }

        // Needs the session saved above for the auth header; a failure doesn't block the login
        deviceTokenRegistrar.registerCurrent()

        // Get profiles info. A failure here doesn't block the login, but leaves it without permissions
        val profile = try {
            profileApi.getUserProfile().also { profile ->
                // Must be stored before the session: SessionStorage reads the profile back from SQLite
                profileLocalDataSource.saveProfile(profile.toLocalProfile())
                profileLocalDataSource.saveAddresses(profile.toWorkplaceAddresses())
                profileLocalDataSource.saveEnterprise(profile.toLocalEnterprise())
                Log.d(TAG, "Profile ${profile.id} stored with permissions ${profile.permissions.map { it.name }}")
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load or store the profile, continuing without permissions", e)
            null
        }

        if (profile != null) enableUnsetNotificationChannels(profile.notifications)

        if (profile != null) {
            val sessionWithPermissions = session.copy(
                permissions = profile.permissions,
                name = profile.name,
                avatar = profile.avatarUrl ?: "",
                createdAt = profile.createdAt
            )
            if (sessionManager.save(sessionWithPermissions)) {
                session = sessionWithPermissions
            } else {
                return Result.failure(AuthException.Unexpected(IllegalStateException("Failed to persist permissions")))
            }
        }

        session
    }

    /**
     * ms-inventory only pushes or e-mails a channel that is explicitly true, and a user who never chose
     * has it null. The settings switches show both on by default, so turn on whichever was never set;
     * a channel the user switched off stays off. A failure doesn't block the login.
     */
    private suspend fun enableUnsetNotificationChannels(current: NotificationPreferencesDto?) {
        val mobile = if (current?.mobile == null) true else null
        val email = if (current?.email == null) true else null
        if (mobile == null && email == null) return
        try {
            profileApi.updateProfile(
                UpdateProfileRequest(notifications = NotificationPreferencesDto(email = email, mobile = mobile))
            )
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to enable the default notification channels", e)
        }
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
            Log.e(TAG, e.message ?: "unexpected error", e)
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
}
