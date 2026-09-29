package com.aether.application.feature.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordSendCodeRequest(
    val email: String
)
