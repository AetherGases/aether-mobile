package com.aether.application.feature.reports.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.auth.domain.model.AppPermission
import com.aether.application.feature.home.domain.model.Inventory
import com.aether.application.feature.home.domain.model.InventoryStatus
import com.aether.application.feature.reports.domain.usecase.GetPendingReportsUseCase
import com.aether.application.feature.reports.domain.usecase.GetReportsUseCase
import com.aether.application.feature.reports.presentation.screen.CalendarDay
import com.aether.application.feature.reports.presentation.screen.ReportItem
import com.aether.application.feature.reports.presentation.screen.ReportTimeFilter
import com.aether.application.feature.reports.presentation.screen.ReportViewerRole
import com.aether.application.feature.reports.presentation.model.toCalendarDay
import com.aether.application.feature.reports.presentation.state.ReportHistoryUiState
import com.aether.core.ui.components.ReportStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

private const val TAG = "ReportHistoryViewModel"

// TODO: no pagination yet, the history only shows the latest PAGE_SIZE reports
private const val PAGE_SIZE = 100

class ReportHistoryViewModel(
    private val getReports: GetReportsUseCase,
    private val getPendingReports: GetPendingReportsUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private data class Entry(
        val item: ReportItem,
        val createdAt: LocalDateTime
    )

    private data class Filters(
        val searchQuery: String = "",
        val timeFilters: Set<ReportTimeFilter> = setOf(ReportTimeFilter.MAIS_RECENTES),
        val statusFilters: Set<ReportStatus> = emptySet(),
        val selectedDate: LocalDate? = null
    )

    private data class LoadState(
        val isRefreshing: Boolean = true,
        val failed: Boolean = false
    )

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private val entries = MutableStateFlow<List<Entry>>(emptyList())
    private val viewerRole = MutableStateFlow(ReportViewerRole.FUNCIONARIO)
    private val filters = MutableStateFlow(Filters())
    private val loadState = MutableStateFlow(LoadState())
    private val displayedMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<ReportHistoryUiState> = combine(
        entries, viewerRole, filters, loadState, displayedMonth
    ) { entries, role, filters, load, month ->
        buildUiState(entries, role, filters, load, month)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ReportHistoryUiState()
    )

    private var loadJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch { load() }
    }

    fun onSearchQueryChanged(query: String) {
        filters.update { it.copy(searchQuery = query) }
    }

    fun onTimeFilterToggled(filter: ReportTimeFilter) {
        filters.update { it.copy(timeFilters = it.timeFilters.toggle(filter)) }
    }

    fun onStatusFilterToggled(status: ReportStatus) {
        filters.update { it.copy(statusFilters = it.statusFilters.toggle(status)) }
    }

    fun onMonthChanged(delta: Long) {
        displayedMonth.update { it.plusMonths(delta) }
    }

    fun onDaySelected(day: CalendarDay) {
        val date = day.date ?: return
        filters.update {
            it.copy(selectedDate = if (it.selectedDate == date) null else date)
        }
    }

    // On failure the previous entries are kept, so a failed refresh doesn't blank the screen
    private suspend fun load() {
        loadState.value = LoadState(isRefreshing = true)
        try {
            val session = sessionManager.getSession()
                ?: throw Exception("nah man, u need to be authed")

            val isAnalyst = sessionManager.hasPermission(AppPermission.INVENTORY_ANALYSIS)
            val canCreate = sessionManager.hasPermission(AppPermission.INVENTORY_CREATING)

            val mine = if (canCreate || !isAnalyst) getReports(PAGE_SIZE) else emptyList()
            val pending = if (isAnalyst) getPendingReports(PAGE_SIZE) else emptyList()
            val pendingIds = pending.map { it.id }.toSet()

            val loaded = (pending + mine)
                .distinctBy { it.id }
                .map { it.toEntry(session.name, session.avatar, it.id in pendingIds) }

            viewerRole.value = if (isAnalyst) ReportViewerRole.GERENTE else ReportViewerRole.FUNCIONARIO
            entries.value = loaded
            loadState.value = LoadState(isRefreshing = false)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load report history", e)
            loadState.value = LoadState(isRefreshing = false, failed = true)
        }
    }

    private fun Inventory.toEntry(
        sessionName: String,
        sessionAvatar: String?,
        isPending: Boolean
    ): Entry {
        val status = status.toHistoryStatus()
        return Entry(
            createdAt = createdAt,
            item = ReportItem(
                id = id.toString(),
                avatarUrl = authorImageUrl ?: sessionAvatar,
                title = name,
                authorName = author ?: sessionName,
                // TODO: the API doesn't send the reviewer yet
                reviewerName = null,
                createdAt = createdAt.format(dateFormatter),
                status = status,
                canReview = isPending && status == ReportStatus.EM_ANALISE
            )
        )
    }

    private fun buildUiState(
        entries: List<Entry>,
        role: ReportViewerRole,
        filters: Filters,
        load: LoadState,
        month: YearMonth
    ): ReportHistoryUiState {
        val datesWithReports = entries.map { it.createdAt.toLocalDate() }.toSet()
        val today = LocalDate.now()

        val query = filters.searchQuery.trim()
        val visible = entries
            .filter { entry ->
                (query.isEmpty() ||
                    entry.item.title.contains(query, ignoreCase = true) ||
                    entry.item.authorName.contains(query, ignoreCase = true)) &&
                    (filters.statusFilters.isEmpty() || entry.item.status in filters.statusFilters) &&
                    (filters.selectedDate == null || entry.createdAt.toLocalDate() == filters.selectedDate)
            }

        // Oldest first only when it is the sole time filter, otherwise newest first
        val oldestFirst = filters.timeFilters == setOf(ReportTimeFilter.MAIS_ANTIGOS)
        val sorted = if (oldestFirst) {
            visible.sortedBy { it.createdAt }
        } else {
            visible.sortedByDescending { it.createdAt }
        }

        val weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        return ReportHistoryUiState(
            viewerRole = role,
            currentWeek = (0L..6L).map {
                weekStart.plusDays(it).toCalendarDay(filters.selectedDate, datesWithReports)
            },
            month = month,
            reportDates = datesWithReports,
            selectedDate = filters.selectedDate,
            reportItems = sorted.map { it.item },
            searchQuery = filters.searchQuery,
            selectedTimeFilters = filters.timeFilters,
            selectedStatusFilters = filters.statusFilters,
            isRefreshing = load.isRefreshing,
            loadFailed = load.failed
        )
    }

    private fun <T> Set<T>.toggle(value: T): Set<T> =
        if (value in this) this - value else this + value
}

private fun InventoryStatus.toHistoryStatus(): ReportStatus = when (this) {
    InventoryStatus.Input.UNDER_REVIEW,
    InventoryStatus.Input.PROCESSING,
    InventoryStatus.Input.PROCESSING_FAILED,
    InventoryStatus.Output.PROCESSING_FAILED -> ReportStatus.EM_ANALISE
    InventoryStatus.Input.APPROVED -> ReportStatus.APROVADO
    InventoryStatus.Input.REJECTED -> ReportStatus.REJEITADO
    InventoryStatus.Output.REQUESTED -> ReportStatus.SOLICITADO
    InventoryStatus.Output.ISSUED -> ReportStatus.EMITIDO
}
