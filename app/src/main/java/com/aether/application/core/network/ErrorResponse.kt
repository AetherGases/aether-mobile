package com.aether.application.core.network

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String? = null
)
