package com.aether.application.feature.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordValidateCodeResponse (
    val key: String
) {
    fun toDomain(): String {
        return key
    }
}