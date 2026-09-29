package com.aether.application.feature.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String? = null
)
