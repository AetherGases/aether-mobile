package com.aether.application.core.auth.model

import com.aether.application.core.serialization.InstantSerializer
import com.aether.application.feature.auth.domain.model.Permission
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Session(
    val email: String,
    val name: String = "Desconhecido",
    val avatar: String = "",
    val accessToken: String,
    val refreshToken: String,
    @Serializable(with = InstantSerializer::class)
    val expiration: Instant,
    /** Null on sessions stored before it was added: [expiration] is used instead. */
    @Serializable(with = InstantSerializer::class)
    val refreshExpiration: Instant? = null,
    val permissions: List<Permission> = emptyList(),
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant? = null
)
