package com.aether.application.feature.notifications.presentation.viewmodel

import android.util.Log
import com.aether.application.core.utils.AppLocale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.R
import com.aether.application.core.utils.UiText
import com.aether.application.core.utils.uiPlural
import com.aether.application.core.utils.uiText
import com.aether.application.feature.home.domain.model.InventoryStatus
import com.aether.application.feature.notifications.domain.model.Notification
import com.aether.application.feature.notifications.domain.repository.NotificationsRepository
import com.aether.application.feature.notifications.presentation.screen.NotificationFilter
import com.aether.application.feature.notifications.presentation.screen.NotificationItem
import com.aether.application.feature.notifications.presentation.screen.NotificationSection
import com.aether.application.feature.notifications.presentation.state.NotificationsUiState
import com.aether.core.ui.components.NotificationStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val TAG = "NotificationsViewModel"

class NotificationsViewModel(
    private val notificationsRepository: NotificationsRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private var notifications: List<Notification> = emptyList()

    init {
        load()
    }

    fun onFilterSelected(filter: NotificationFilter) {
        _uiState.update { it.copy(selectedFilter = filter, sections = buildSections(filter)) }
    }

    fun refresh() {
        if (_uiState.value.isRefreshing) return
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            val fetched = fetchOrNull()
            val changed = fetched != null && fetched != notifications
            if (changed) notifications = fetched
            _uiState.update {
                it.copy(
                    sections = if (changed) buildSections(it.selectedFilter) else it.sections,
                    isRefreshing = false,
                    failed = fetched == null
                )
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            val fetched = fetchOrNull()
            if (fetched != null) notifications = fetched
            _uiState.update {
                it.copy(
                    sections = buildSections(it.selectedFilter),
                    isLoading = false,
                    failed = fetched == null
                )
            }
        }
    }

    private suspend fun fetchOrNull(): List<Notification>? =
        try {
            notificationsRepository.getNotifications()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load notifications", e)
            null
        }

    private fun buildSections(filter: NotificationFilter): List<NotificationSection> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val now = Instant.now()

        val ordered = when (filter) {
            NotificationFilter.ANTIGAS -> notifications.sortedBy { it.timestamp }
            NotificationFilter.RECENTES -> notifications.sortedByDescending { it.timestamp }
        }

        return ordered
            .groupBy { sectionTitle(it.timestamp.atZone(zone).toLocalDate(), today) }
            .map { (title, group) ->
                NotificationSection(
                    title = title,
                    items = group.map { it.toItem(zone, now) }
                )
            }
    }

    private fun sectionTitle(date: LocalDate, today: LocalDate): UiText = when {
        date == today -> uiText(R.string.notifications_section_today)
        !date.isAfter(today) && date.isAfter(today.minusDays(7)) -> uiText(R.string.notifications_section_this_week)
        date.year == today.year && date.month == today.month -> uiText(R.string.notifications_section_this_month)
        else -> uiText(R.string.notifications_section_earlier)
    }

    private fun Notification.toItem(zone: ZoneId, now: Instant) = NotificationItem(
        reportId = reportId,
        status = status.toNotificationStatus(),
        title = title,
        reviewer = description,
        timestamp = AppLocale.dayMonthTimeFormatter().format(timestamp.atZone(zone)),
        relativeTime = relativeTime(timestamp, now)
    )

    private fun InventoryStatus.toNotificationStatus() = when (this) {
        InventoryStatus.Input.APPROVED -> NotificationStatus.APROVADO
        InventoryStatus.Input.REJECTED -> NotificationStatus.REPROVADO
        else -> NotificationStatus.PENDENTE
    }

    private fun relativeTime(timestamp: Instant, now: Instant): UiText {
        val elapsed = Duration.between(timestamp, now)
        return when {
            elapsed.toMinutes() < 1 -> uiText(R.string.notifications_time_now)
            elapsed.toHours() < 1 -> uiText(R.string.notifications_time_minutes_ago, elapsed.toMinutes())
            elapsed.toDays() < 1 -> uiPlural(R.plurals.notifications_time_hours_ago, elapsed.toHours().toInt())
            else -> uiPlural(R.plurals.notifications_time_days_ago, elapsed.toDays().toInt())
        }
    }
}
