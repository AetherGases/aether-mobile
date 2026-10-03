package com.aether.application.feature.auth.data.remote.dto

import com.aether.application.feature.auth.domain.model.Permission
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    val id: Int,
    val cpf: String,
    val email: String,
    val name: String,
    val phone: String,
    val permissions: List<Permission>
)
