package com.aether.application.core.notification.domain

interface DeviceTokenRegistrar {
    /** Sends the device's FCM token to the backend; the stored one first, a fresh one if there is none. */
    suspend fun registerCurrent()

    /** Stores a rotated token and, when there is a session, sends it to the backend. */
    suspend fun onTokenRefreshed(token: String)
}
