package com.aether.application.feature.profile.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Response of ms-cloudinary `GET /api/cloudinary/upload-signature`. */
@Serializable
data class UploadSignatureResponse(
    val signature: String,
    val timestamp: Long,
    val apiKey: String,
    val cloudName: String,
    val cloudinaryFolder: String,
    val cloudinaryResourceType: String,
)

/** Subset of Cloudinary's upload response the app uses. */
@Serializable
data class CloudinaryUploadResponse(
    @SerialName("secure_url")
    val secureUrl: String,
)

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
