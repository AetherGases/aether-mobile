package com.aether.application.feature.auth.data.remote.dto

import java.util.Date

data class LoginResponse(
    val email: String,
    val authenticated: Boolean,
    val created: Date,
    val expiration: Date,
    val accessToken: String,
    val refreshToken: String
)
