package com.aether.application.feature.notifications.domain.repository

import com.aether.application.feature.notifications.domain.model.Notification

interface NotificationsRepository {
    suspend fun getNotifications(): List<Notification>
}
