package com.aether.application.core.network

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String? = null,
    // FastAPI services (ms-aeko-hub) describe the error in `detail`
    val detail: String? = null
)
