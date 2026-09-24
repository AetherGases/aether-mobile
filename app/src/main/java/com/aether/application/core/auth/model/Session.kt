package com.aether.application.core.auth.model

import com.aether.application.core.serialization.InstantSerializer
import com.aether.application.feature.auth.domain.model.Permission
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Session(
    val email: String,
    val accessToken: String,
    val refreshToken: String,
    @Serializable(with = InstantSerializer::class)
    val expiration: Instant,
    val permissions: List<Permission> = emptyList()
)
