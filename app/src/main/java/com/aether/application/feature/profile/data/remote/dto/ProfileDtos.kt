package com.aether.application.feature.profile.data.remote.dto

import kotlinx.serialization.Serializable

/** Body of ms-auth `PATCH /api/profile`; null fields are left unchanged. */
@Serializable
data class UpdateProfileRequest(
    val image: ImageRequest? = null,
)

@Serializable
data class ImageRequest(
    val imageName: String,
    val imageUrl: String,
)
