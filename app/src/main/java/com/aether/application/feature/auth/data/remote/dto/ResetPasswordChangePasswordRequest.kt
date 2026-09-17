package com.aether.application.feature.auth.data.remote.dto

data class ResetPasswordChangePasswordRequest(
    val email: String,
    val password: String,
    val key: String
)
