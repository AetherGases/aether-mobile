package com.aether.application.feature.auth.data.remote

import com.aether.application.feature.auth.data.remote.dto.LoginRequest
import com.aether.application.feature.auth.data.remote.dto.LoginResponse
import com.aether.application.feature.auth.data.remote.dto.ResetPasswordChangePasswordRequest
import com.aether.application.feature.auth.data.remote.dto.ResetPasswordSendCodeRequest
import com.aether.application.feature.auth.data.remote.dto.ResetPasswordValidateCodeRequest
import com.aether.application.feature.auth.data.remote.dto.ResetPasswordValidateCodeResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

private const val AUTH = "auth"

interface AuthApi {

    @POST("$AUTH/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @PUT("$AUTH/refresh/{email}")
    suspend fun refreshToken(
        @Path("email") email: String,
        @Header("Authorization") refreshToken: String
    ): LoginResponse

    @POST("$AUTH/reset-password/send-code")
    suspend fun resetPasswordSendCode(
        @Body request: ResetPasswordSendCodeRequest
    )

    @POST("$AUTH/reset-password/validate-code")
    suspend fun resetPasswordValidateCode(
        @Body request: ResetPasswordValidateCodeRequest
    ): ResetPasswordValidateCodeResponse

    @POST("$AUTH/reset-password/change-password")
    suspend fun resetPasswordChangePassword(
        @Body request: ResetPasswordChangePasswordRequest
    )
}