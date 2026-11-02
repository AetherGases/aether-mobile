package com.aether.application.feature.profile.domain.repository

import android.net.Uri
import com.aether.application.core.auth.model.Session

interface ProfileRepository {

    suspend fun updateAvatar(uri: Uri): String

    /**
     * Fetches the profile and its permissions, stores them and updates the session with them.
     * Throws when it can't be loaded, which is different from a profile without permissions.
     */
    suspend fun refreshProfile(): Session
}
