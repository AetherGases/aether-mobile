package com.aether.application.feature.notifications.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.feature.home.domain.model.InventoryStatus
import com.aether.application.feature.notifications.domain.model.Notification
import com.aether.application.feature.notifications.domain.repository.NotificationsRepository
import com.aether.application.feature.notifications.presentation.screen.NotificationFilter
import com.aether.application.feature.notifications.presentation.screen.NotificationItem
import com.aether.application.feature.notifications.presentation.screen.NotificationSection
import com.aether.application.feature.notifications.presentation.state.NotificationsUiState
import com.aether.application.core.ui.components.NotificationStatus
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
    private val timestampFormatter = DateTimeFormatter.ofPattern("dd/MM HH:mm")

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
                    isRefreshing = false
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

    private fun sectionTitle(date: LocalDate, today: LocalDate): String = when {
        date == today -> "Hoje"
        !date.isAfter(today) && date.isAfter(today.minusDays(7)) -> "Essa semana"
        date.year == today.year && date.month == today.month -> "Esse mês"
        else -> "Anteriores"
    }

    private fun Notification.toItem(zone: ZoneId, now: Instant) = NotificationItem(
        reportId = reportId,
        status = status.toNotificationStatus(),
        title = title,
        reviewer = description,
        timestamp = timestampFormatter.format(timestamp.atZone(zone)),
        relativeTime = relativeTime(timestamp, now)
    )

    private fun InventoryStatus.toNotificationStatus() = when (this) {
        InventoryStatus.Input.APPROVED -> NotificationStatus.APROVADO
        InventoryStatus.Input.REJECTED -> NotificationStatus.REPROVADO
        else -> NotificationStatus.PENDENTE
    }

    private fun relativeTime(timestamp: Instant, now: Instant): String {
        val elapsed = Duration.between(timestamp, now)
        return when {
            elapsed.toMinutes() < 1 -> "Agora"
            elapsed.toHours() < 1 -> "Há ${elapsed.toMinutes()} min"
            elapsed.toDays() < 1 -> "Há ${elapsed.toHours()} ${if (elapsed.toHours() == 1L) "hora" else "horas"}"
            else -> "Há ${elapsed.toDays()} ${if (elapsed.toDays() == 1L) "dia" else "dias"}"
        }
    }
}
