package com.aether.application.feature.auth.data.remote.dto

import com.aether.application.core.auth.model.Session
import com.aether.application.core.serialization.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class LoginResponse(
    val email: String,
    val authenticated: Boolean,
    @Serializable(with = InstantSerializer::class)
    val created: Instant,
    @Serializable(with = InstantSerializer::class)
    val expiration: Instant,
    val accessToken: String,
    val refreshToken: String
) {
    fun toDomain(): Session {
        return Session(
            email = email,
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiration = expiration
        )
    }
}
