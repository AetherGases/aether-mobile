package com.aether.application.feature.reports.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.core.auth.model.Session
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.auth.domain.model.AppPermission
import com.aether.application.feature.home.domain.model.Inventory
import com.aether.application.feature.home.domain.model.InventoryStatus
import com.aether.application.feature.reports.domain.model.ReportsPage
import com.aether.application.feature.reports.domain.usecase.GetReportsUseCase
import com.aether.application.feature.reports.domain.usecase.GetReviewedReportsUseCase
import com.aether.application.feature.reports.presentation.screen.CalendarDay
import com.aether.application.feature.reports.presentation.screen.ReportItem
import com.aether.application.feature.reports.presentation.screen.ReportTimeFilter
import com.aether.application.feature.reports.presentation.screen.ReportViewerRole
import com.aether.application.feature.reports.presentation.model.toCalendarDay
import com.aether.application.feature.reports.presentation.state.ReportHistoryUiState
import com.aether.core.ui.components.ReportStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

// The backend rejects a `take` above 6 (MAX_TAKE in aether-ms-inventory)
private const val PAGE_SIZE = 6

class ReportHistoryViewModel(
    private val getReports: GetReportsUseCase,
    private val getReviewedReports: GetReviewedReportsUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    /**
     * Reviewers see their whole department through the reviewed route, pending ones included;
     * everyone else sees only their own reports.
     */
    private enum class Kind { MINE, DEPARTMENT }

    /** One paged list on the server. */
    private class Source(
        val kind: Kind,
        private val fetch: suspend (skip: Int, take: Int) -> ReportsPage
    ) {
        private var skip = 0
        private var total = Long.MAX_VALUE
        val hasMore get() = skip < total

        suspend fun next(): List<Pair<Kind, Inventory>> {
            val page = fetch(skip, PAGE_SIZE)
            skip += page.reports.size
            // An empty page ends the source even if the server count says otherwise
            total = if (page.reports.isEmpty()) skip.toLong() else page.totalCount
            return page.reports.map { kind to it }
        }
    }

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
        val failed: Boolean = false,
        val isLoadingMore: Boolean = false,
        val loadMoreFailed: Boolean = false,
        val hasMore: Boolean = false
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
        if (loadJob?.isActive == true || loadState.value.isLoadingMore) return
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

    private var sources: List<Source> = emptyList()

    fun loadMore() {
        val state = loadState.value
        if (!state.hasMore || state.isRefreshing || state.isLoadingMore) return
        loadState.update { it.copy(isLoadingMore = true, loadMoreFailed = false) }
        loadJob = viewModelScope.launch { loadNext() }
    }

    // On failure the previous entries are kept, so a failed refresh doesn't blank the screen
    private suspend fun load() {
        loadState.update { it.copy(isRefreshing = true, failed = false, loadMoreFailed = false) }
        try {
            val session = sessionManager.getSession()
                ?: throw Exception("nah man, u need to be authed")

            val isAnalyst = sessionManager.hasPermission(AppPermission.INVENTORY_ANALYSIS)

            val newSources = listOf(
                if (isAnalyst) {
                    Source(Kind.DEPARTMENT) { skip, take -> getReviewedReports(skip, take) }
                } else {
                    Source(Kind.MINE) { skip, take -> getReports(skip, take) }
                }
            )
            val firstPage = coroutineScope {
                newSources.map { source -> async { source.next() } }.awaitAll()
            }.flatten()

            sources = newSources
            viewerRole.value = if (isAnalyst) ReportViewerRole.GERENTE else ReportViewerRole.FUNCIONARIO
            entries.value = merge(emptyList(), firstPage, session)
            loadState.update { it.copy(isRefreshing = false, hasMore = newSources.any { s -> s.hasMore }) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load report history", e)
            loadState.update { it.copy(isRefreshing = false, failed = true) }
        }
    }

    private suspend fun loadNext() {
        val session = sessionManager.getSession()
        if (session == null) {
            loadState.update { it.copy(isLoadingMore = false) }
            return
        }
        try {
            // Sources are independent: one failing doesn't drop what the others returned,
            // and it retries from the same offset next time
            val results = coroutineScope {
                sources.filter { it.hasMore }.map { source ->
                    async {
                        try {
                            source.next()
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to load more ${source.kind} reports", e)
                            null
                        }
                    }
                }.awaitAll()
            }
            entries.update { merge(it, results.filterNotNull().flatten(), session) }
            loadState.update {
                it.copy(
                    isLoadingMore = false,
                    loadMoreFailed = results.any { r -> r == null },
                    hasMore = sources.any { s -> s.hasMore }
                )
            }
        } catch (e: CancellationException) {
            loadState.update { it.copy(isLoadingMore = false) }
            throw e
        }
    }

    /** Appends [fetched] to [existing]; a report that shifted to another page keeps its first place. */
    private fun merge(
        existing: List<Entry>,
        fetched: List<Pair<Kind, Inventory>>,
        session: Session
    ): List<Entry> {
        val byId = LinkedHashMap<String, Entry>()
        existing.forEach { byId[it.item.id] = it }
        fetched.forEach { (kind, inventory) ->
            val entry = inventory.toEntry(session, kind)
            byId[entry.item.id] = entry
        }
        return byId.values.toList()
    }

    private fun Inventory.toEntry(session: Session, kind: Kind): Entry {
        return Entry(
            createdAt = createdAt,
            item = ReportItem(
                id = id.toString(),
                avatarUrl = authorImageUrl,
                title = name,
                authorName = author ?: "Desconhecido",
                reviewerName = reviewer,
                createdAt = createdAt.format(dateFormatter),
                status = status.toHistoryStatus(),
                canReview = kind == Kind.DEPARTMENT && status == InventoryStatus.Input.UNDER_REVIEW
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
            loadFailed = load.failed,
            isLoadingMore = load.isLoadingMore,
            loadMoreFailed = load.loadMoreFailed,
            hasMore = load.hasMore
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
