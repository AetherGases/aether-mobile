package com.aether.application.feature.profile.domain.repository

import android.net.Uri

interface ProfileRepository {

    suspend fun updateAvatar(uri: Uri): String
}
