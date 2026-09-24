package com.aether.application.feature.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Permission(
    val id: Int,
    val name: String,
    val description: String? = null
)
