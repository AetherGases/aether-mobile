package com.aether.application.feature.auth.data.remote.dto

import com.aether.application.feature.auth.domain.model.Permission
import com.aether.application.core.serialization.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class UserProfileResponse(
    val id: Long,
    val cpf: String,
    val email: String,
    val name: String,
    val avatarUrl: String? = null, // TODO(confirmar nome no back)
    val phone: String,
    val permissions: List<Permission>,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant? = null, // TODO(confirmar nome no back)
    val enterprise: EnterpriseDto? = null
) {
    @Serializable
    data class EnterpriseDto(val id: Long)
}
