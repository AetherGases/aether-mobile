package com.aether.application.feature.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordValidateCodeRequest(
    val email: String,
    val code: String
)
