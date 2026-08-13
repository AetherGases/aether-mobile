package com.aether.application.feature.auth.data.remote.dto

data class LoginRequest(
    val email: String,
    val password: String
)
