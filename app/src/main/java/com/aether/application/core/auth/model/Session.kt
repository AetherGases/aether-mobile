package com.aether.application.core.auth.model

import java.time.Instant

data class Session(
    val email: String,
    val accessToken: String,
    val refreshToken: String,
    val expiration: Instant
)
