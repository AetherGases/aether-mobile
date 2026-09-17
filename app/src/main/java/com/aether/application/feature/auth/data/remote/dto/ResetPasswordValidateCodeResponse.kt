package com.aether.application.feature.auth.data.remote.dto

import com.aether.application.core.auth.model.Session
import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordValidateCodeResponse (
    val key: String
) {
}