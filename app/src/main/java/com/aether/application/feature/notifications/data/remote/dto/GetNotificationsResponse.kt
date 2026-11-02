package com.aether.application.feature.notifications.data.remote.dto

import com.aether.application.core.serialization.InstantSerializer
import com.aether.application.feature.home.domain.model.InventoryStatus
import com.aether.application.feature.home.domain.model.inventoryStatusOf
import com.aether.application.feature.notifications.domain.model.Notification
import kotlinx.serialization.Serializable
import java.time.Instant

/** One item of ms-auth `GET /api/profile/notifications`. */
@Serializable
data class NotificationResponse(
    /** Mongo id of the notification itself; not used, since the app navigates by [inventory]`.id`. */
    val id: String? = null,
    val title: String,
    val description: String,
    val inventory: NotificationInventoryResponse? = null,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant
) {
    fun toDomain() = Notification(
        reportId = inventory?.id,
        title = title,
        description = description,
        status = inventory
            ?.let { inventoryStatusOf(it.status, it.type.orEmpty()) }
            ?: InventoryStatus.Input.UNDER_REVIEW,
        timestamp = createdAt
    )
}

/** The inventory a notification is about; its [id] is what opens the report. */
@Serializable
data class NotificationInventoryResponse(
    val id: Long,
    val status: String,
    val type: String? = null
)

@Serializable
data class GetNotificationsResponse(
    val notifications: List<NotificationResponse>,
    val total: Long = 0,
    val skip: Int = 0,
    val take: Int = 0
)
