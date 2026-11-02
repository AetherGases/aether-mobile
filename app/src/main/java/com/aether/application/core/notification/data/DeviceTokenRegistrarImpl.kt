package com.aether.application.core.notification.data

import android.util.Log
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.core.notification.domain.DeviceTokenRegistrar
import com.aether.application.core.notification.domain.FcmTokenStorage
import com.aether.application.feature.auth.data.remote.ProfileApi
import com.aether.application.feature.profile.data.remote.dto.UpdateProfileRequest
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

private const val TAG = "DeviceTokenRegistrar"
private const val TOKEN_FETCH_TIMEOUT_MS = 5_000L

class DeviceTokenRegistrarImpl(
    private val tokenStorage: FcmTokenStorage,
    private val profileApi: ProfileApi,
    private val sessionManager: SessionManager
): DeviceTokenRegistrar {

    override suspend fun registerCurrent() {
        val token = tokenStorage.getToken() ?: withTimeoutOrNull(TOKEN_FETCH_TIMEOUT_MS) { fetchToken() }?.also { tokenStorage.saveToken(it) }
        if (token == null) {
            Log.w(TAG, "No FCM token available, skipping device registration")
            return
        }
        send(token)
    }

    override suspend fun onTokenRefreshed(token: String) {
        tokenStorage.saveToken(token)
        if (sessionManager.getSession() != null) send(token)
    }

    private suspend fun send(token: String) {
        try {
            profileApi.updateProfile(UpdateProfileRequest(deviceToken = token))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register the device token", e)
        }
    }

    private suspend fun fetchToken(): String? = suspendCancellableCoroutine { continuation ->
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { continuation.resume(it) }
            .addOnFailureListener {
                Log.e(TAG, "Failed to fetch the FCM token", it)
                continuation.resume(null)
            }
    }
}
