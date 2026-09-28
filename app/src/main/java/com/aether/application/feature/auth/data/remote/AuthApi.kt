package com.aether.application.feature.auth.data.remote

import com.aether.application.feature.auth.data.remote.dto.LoginRequest
import com.aether.application.feature.auth.data.remote.dto.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApi {

    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    // TODO
    @GET("auth/signUp")
    suspend fun signUp()

    @PUT("auth/refresh/{email}")
    suspend fun refreshToken(
        @Path("email") email: String,
        @Header("Authorization") refreshToken: String
    ): LoginResponse
}
