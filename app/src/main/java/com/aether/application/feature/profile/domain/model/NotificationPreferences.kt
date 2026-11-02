package com.aether.application.feature.profile.domain.model

/** Channels the backend may notify the user on. A channel never chosen on the server counts as off. */
data class NotificationPreferences(
    val mobile: Boolean = false,
    val email: Boolean = false,
)
