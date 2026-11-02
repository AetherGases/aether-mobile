package com.aether.application.feature.notifications.data.repository

import com.aether.application.feature.notifications.data.remote.NotificationsApi
import com.aether.application.feature.notifications.domain.model.Notification
import com.aether.application.feature.notifications.domain.repository.NotificationsRepository

class NotificationsRepositoryImpl(
    private val api: NotificationsApi
): NotificationsRepository {
    override suspend fun getNotifications(): List<Notification> {
        return api.getNotifications().notifications.map { it.toDomain() }
    }
}
