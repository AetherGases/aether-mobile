package com.aether.application.feature.profile.domain.repository

import android.net.Uri
import com.aether.application.core.auth.model.Session
import com.aether.application.feature.profile.domain.model.NotificationPreferences

interface ProfileRepository {

    suspend fun updateAvatar(uri: Uri): String

    /**
     * Fetches the profile and its permissions, stores them and updates the session with them.
     * Throws when it can't be loaded, which is different from a profile without permissions.
     */
    suspend fun refreshProfile(): Session

    /** The push and e-mail switches stored on the server (`GET /api/profile`). */
    suspend fun getNotificationPreferences(): NotificationPreferences

    /** Changes only the channels given (`PATCH /api/profile`); null leaves a channel as it is. */
    suspend fun setNotificationPreferences(mobile: Boolean? = null, email: Boolean? = null)
}
