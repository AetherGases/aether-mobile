package com.aether.application.feature.auth.data.remote

import com.aether.application.feature.auth.data.remote.dto.LoginRequest
import com.aether.application.feature.auth.data.remote.dto.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path

interface AuthAPI {

    @GET("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    // TODO
    @GET("auth/signUp")
    suspend fun signUp()

    // TODO
    @GET("refresh/{email}")
    suspend fun refreshToken(@Path("email") email: String)
}