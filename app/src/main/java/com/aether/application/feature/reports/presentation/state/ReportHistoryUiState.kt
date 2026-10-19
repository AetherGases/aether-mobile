package com.aether.application.feature.reports.presentation.state

import com.aether.application.feature.reports.presentation.screen.CalendarDay
import com.aether.application.feature.reports.presentation.screen.ReportItem
import com.aether.application.feature.reports.presentation.screen.ReportTimeFilter
import com.aether.application.feature.reports.presentation.screen.ReportViewerRole
import com.aether.core.ui.components.ReportStatus
import java.time.LocalDate
import java.time.YearMonth

data class ReportHistoryUiState(
    val viewerRole: ReportViewerRole = ReportViewerRole.FUNCIONARIO,
    val currentWeek: List<CalendarDay?> = emptyList(),
    val month: YearMonth = YearMonth.now(),
    val reportDates: Set<LocalDate> = emptySet(),
    val selectedDate: LocalDate? = null,
    val reportItems: List<ReportItem> = emptyList(),
    val searchQuery: String = "",
    val selectedTimeFilters: Set<ReportTimeFilter> = setOf(ReportTimeFilter.MAIS_RECENTES),
    val selectedStatusFilters: Set<ReportStatus> = emptySet(),
    val isRefreshing: Boolean = true,
    val loadFailed: Boolean = false,
    val isLoadingMore: Boolean = false,
    val loadMoreFailed: Boolean = false,
    val hasMore: Boolean = false,
)
