package com.aether.application.feature.auth.data.remote.dto

data class ResetPasswordValidateCodeRequest(
    val email: String,
    val code: String
)
