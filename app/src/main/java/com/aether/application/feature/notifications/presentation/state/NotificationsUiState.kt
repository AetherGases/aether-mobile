package com.aether.application.feature.notifications.presentation.state

import com.aether.application.feature.notifications.presentation.screen.NotificationFilter
import com.aether.application.feature.notifications.presentation.screen.NotificationSection

data class NotificationsUiState(
    val sections: List<NotificationSection> = emptyList(),
    val selectedFilter: NotificationFilter = NotificationFilter.RECENTES,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val failed: Boolean = false
)
