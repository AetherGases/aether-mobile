package com.aether.application.feature.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordChangePasswordRequest(
    val email: String,
    val password: String,
    val key: String
)
