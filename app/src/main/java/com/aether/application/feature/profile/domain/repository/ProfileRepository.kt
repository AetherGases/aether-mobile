package com.aether.application.feature.profile.domain.repository

import android.net.Uri

interface ProfileRepository {

    /** Uploads the photo at [uri] as the user's avatar and returns its new url. */
    suspend fun updateAvatar(uri: Uri): String
}
