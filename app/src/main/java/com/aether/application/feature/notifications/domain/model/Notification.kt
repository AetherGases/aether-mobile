package com.aether.application.feature.notifications.domain.model

import com.aether.application.feature.home.domain.model.InventoryStatus
import java.time.Instant

data class Notification(
    val reportId: Long?,
    val title: String,
    val description: String,
    val status: InventoryStatus,
    val timestamp: Instant
)