package com.aether.application.feature.notifications.data.remote.dto

import com.aether.application.core.serialization.InstantSerializer
import com.aether.application.feature.home.domain.model.inventoryStatusOf
import com.aether.application.feature.notifications.domain.model.Notification
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class NotificationResponse(
    val id: Long? = null,
    val title: String,
    val description: String,
    val status: String,
    @Serializable(with = InstantSerializer::class)
    val timestamp: Instant
) {
    fun toDomain() = Notification(
        reportId = id,
        title = title,
        description = description,
        status = inventoryStatusOf(status, "input"),
        timestamp = timestamp
    )
}

@Serializable
data class GetNotificationsResponse(
    val notifications: List<NotificationResponse>
)