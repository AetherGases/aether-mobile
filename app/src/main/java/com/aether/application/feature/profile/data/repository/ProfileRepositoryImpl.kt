package com.aether.application.feature.profile.data.repository

import android.net.Uri
import com.aether.application.core.auth.model.Session
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.core.database.ProfileLocalDataSource
import com.aether.application.core.network.cloudinary.CloudinaryApi
import com.aether.application.core.network.cloudinary.CloudinaryFileType
import com.aether.application.core.network.cloudinary.CloudinaryUploader
import com.aether.application.feature.auth.data.remote.ProfileApi
import com.aether.application.feature.profile.data.local.AvatarImageProcessor
import com.aether.application.feature.profile.data.remote.dto.ImageRequest
import com.aether.application.feature.profile.data.remote.dto.UpdateProfileRequest
import com.aether.application.feature.profile.domain.repository.ProfileRepository

private const val AVATAR_MIME_TYPE = "image/jpeg"

class ProfileRepositoryImpl(
    private val profileApi: ProfileApi,
    private val cloudinaryApi: CloudinaryApi,
    private val cloudinaryUploader: CloudinaryUploader,
    private val avatarImageProcessor: AvatarImageProcessor,
    private val profileLocalDataSource: ProfileLocalDataSource,
    private val sessionManager: SessionManager
) : ProfileRepository {

    override suspend fun updateAvatar(uri: Uri): String {
        val bytes = avatarImageProcessor.toJpeg(uri)
        val fileName = "avatar_${System.currentTimeMillis()}.jpg"

        val signature = cloudinaryApi.getUploadSignature(fileType = CloudinaryFileType.IMAGE)
        val imageUrl = cloudinaryUploader.upload(
            bytes = bytes,
            fileName = fileName,
            mimeType = AVATAR_MIME_TYPE,
            signature = signature
        )

        profileApi.updateProfile(
            UpdateProfileRequest(
                image = ImageRequest(imageName = fileName, imageUrl = imageUrl)
            )
        )

        profileLocalDataSource.updateAvatar(imageUrl)
        sessionManager.getSession()?.let { session ->
            sessionManager.save(session.copy(avatar = imageUrl))
        }
        return imageUrl
    }

    override suspend fun refreshProfile(): Session {
        val profile = profileApi.getUserProfile()
        // Must be stored before the session: SessionStorage reads the profile back from SQLite
        profileLocalDataSource.saveProfile(profile.toLocalProfile())
        profileLocalDataSource.saveAddresses(profile.toWorkplaceAddresses())
        profileLocalDataSource.saveEnterprise(profile.toLocalEnterprise())

        val session = checkNotNull(sessionManager.getSession()) { "No session to update" }
        val updated = session.copy(
            userId = profile.id,
            permissions = profile.permissions,
            name = profile.name,
            avatar = profile.avatarUrl ?: "",
            createdAt = profile.createdAt
        )
        check(sessionManager.save(updated)) { "Failed to persist the profile" }
        return updated
    }
}
