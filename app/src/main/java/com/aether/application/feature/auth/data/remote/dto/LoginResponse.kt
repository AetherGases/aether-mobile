package com.aether.application.feature.auth.data.remote.dto

import com.aether.application.core.auth.model.Session
import java.util.Date

data class LoginResponse(
    val email: String,
    val authenticated: Boolean,
    val created: Date,
    val expiration: Date,
    val accessToken: String,
    val refreshToken: String
) {
    fun toDomain(): Session {
        return Session(
            email = email,
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiration = expiration.toInstant()
        )
    }
}
