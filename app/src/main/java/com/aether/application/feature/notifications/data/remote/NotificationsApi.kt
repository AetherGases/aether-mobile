package com.aether.application.feature.notifications.data.remote

import com.aether.application.feature.notifications.data.remote.dto.GetNotificationsResponse
import retrofit2.http.GET

private const val NOTIFICATIONS = "api/profile"

interface NotificationsApi {
    @GET("${NOTIFICATIONS}/notifications")
    suspend fun getNotifications(): GetNotificationsResponse
}