package com.aether.application.feature.auth.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface AuthAPI {

    @GET("auth/login")
    suspend fun login()

    // TODO
    @GET("auth/signUp")
    suspend fun signUp()

    // TODO
    @GET("refresh/{email}")
    suspend fun signUp(@Path("email") email: String)
}