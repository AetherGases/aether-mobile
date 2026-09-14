package com.aether.application.core.auth.model

import com.aether.application.core.serialization.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Session(
    val email: String,
    val accessToken: String,
    val refreshToken: String,
    @Serializable(with = InstantSerializer::class)
    val expiration: Instant
)
