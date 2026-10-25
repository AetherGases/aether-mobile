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
    val phone: String?,
    val image: ImageDto? = null,
    val permissions: List<Permission>,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant? = null, // TODO(confirmar nome no back)
    val enterprise: EnterpriseDto? = null
) {
    val avatarUrl: String?
        get() = image?.path

    @Serializable
    data class ImageDto(val id: Long, val name: String, val path: String)

    @Serializable
    data class EnterpriseDto(val id: Long)
}
