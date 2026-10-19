package com.aether.application.feature.reports.presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.aether.application.R
import com.aether.application.feature.reports.presentation.model.buildMonthWeeks
import com.aether.application.feature.reports.presentation.model.label
import com.aether.core.ui.components.AetherTopBar
import com.aether.core.ui.components.ReportCard
import com.aether.core.ui.components.ReportStatus
import com.aether.core.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth

enum class ReportViewerRole { GERENTE, FUNCIONARIO }

enum class ReportTimeFilter(val label: String) {
    MAIS_RECENTES("Mais Recentes"),
    MAIS_ANTIGOS("Mais Antigos")
}

private val ReportStatus.filterLabel: String
    get() = when (this) {
        ReportStatus.EM_ANALISE -> "Em análise"
        ReportStatus.APROVADO -> "Aprovados"
        ReportStatus.REJEITADO -> "Rejeitados"
        ReportStatus.EMITIDO -> "Emitidos"
        ReportStatus.SOLICITADO -> "Solicitados"
    }

data class ReportItem(
    val id: String,
    val avatarUrl: String?,
    val title: String,
    val authorName: String,
    val reviewerName: String?,
    val createdAt: String,
    val status: ReportStatus,
    val canReview: Boolean = false
)

data class CalendarDay(
    val number: String,
    val isSelected: Boolean = false,
    val isEnabled: Boolean = true,
    val date: LocalDate? = null
)

private const val SWIPE_THRESHOLD = 80f

private val weekDayLetters = listOf("D", "S", "T", "Q", "Q", "S", "S")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportHistoryScreen(
    viewerRole: ReportViewerRole,
    currentWeek: List<CalendarDay?>,
    month: YearMonth,
    reportDates: Set<LocalDate>,
    selectedDate: LocalDate?,
    reportItems: List<ReportItem>,
    searchQuery: String,
    selectedTimeFilters: Set<ReportTimeFilter>,
    selectedStatusFilters: Set<ReportStatus>,
    isRefreshing: Boolean,
    loadFailed: Boolean,
    onRefresh: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onTimeFilterToggled: (ReportTimeFilter) -> Unit,
    onStatusFilterToggled: (ReportStatus) -> Unit,
    onDaySelected: (CalendarDay) -> Unit,
    onMonthChanged: (Long) -> Unit,
    onReviewClick: (String) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var filterPanelVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AetherTopBar(title = "Gerenciamento de relatórios", onBackClick = onBackClicked)

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            Text("Histórico", style = titleLarge, color = MaterialTheme.colorScheme.onBackground)

            Spacer(Modifier.height(16.dp))

            ReportCalendarCard(
                currentWeek = currentWeek,
                month = month,
                reportDates = reportDates,
                selectedDate = selectedDate,
                onDaySelected = onDaySelected,
                onMonthChanged = onMonthChanged
            )

            Spacer(Modifier.height(20.dp))

            Box {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        placeholder = { Text("Procurar relatório") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_search),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default,
                        shape = RoundedCornerShape(percent = 50),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    )

                    Spacer(Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                                        MaterialTheme.colorScheme.secondaryContainer
                                    )
                                )
                            )
                            .clickable { filterPanelVisible = !filterPanelVisible },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_tune),
                            contentDescription = "Filtrar",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (filterPanelVisible && !LocalInspectionMode.current) {
                    val offsetY = with(LocalDensity.current) { 60.dp.roundToPx() }
                    Popup(
                        alignment = Alignment.TopEnd,
                        offset = IntOffset(x = 0, y = offsetY),
                        onDismissRequest = { filterPanelVisible = false }
                    ) {
                        ReportFilterPanel(
                            selectedTimeFilters = selectedTimeFilters,
                            selectedStatusFilters = selectedStatusFilters,
                            onTimeFilterToggled = onTimeFilterToggled,
                            onStatusFilterToggled = onStatusFilterToggled
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            if (reportItems.isEmpty() && !isRefreshing) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.empty_inventory),
                        contentDescription = null
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = if (loadFailed) {
                            "Não foi possível carregar os relatórios. Puxe para baixo para tentar de novo."
                        } else {
                            "Nenhum relatório encontrado."
                        },
                        style = bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            reportItems.forEachIndexed { index, item ->
                val secondaryLabel = when (viewerRole) {
                    ReportViewerRole.GERENTE -> "Realizado por ${item.authorName}"
                    ReportViewerRole.FUNCIONARIO ->
                        item.reviewerName?.let { "Revisado por $it" } ?: "Revisor não informado"
                }

                ReportCard(
                    avatarUrl = item.avatarUrl,
                    title = item.title,
                    secondaryLabel = secondaryLabel,
                    createdAt = item.createdAt,
                    status = item.status,
                    onReviewClick = if (item.canReview) {
                        { onReviewClick(item.id) }
                    } else {
                        null
                    }
                )

                if (index != reportItems.lastIndex) {
                    Spacer(Modifier.height(14.dp))
                }
            }

            Spacer(Modifier.height(24.dp))
        }
        }
    }
}

@Composable
private fun ReportCalendarCard(
    currentWeek: List<CalendarDay?>,
    month: YearMonth,
    reportDates: Set<LocalDate>,
    selectedDate: LocalDate?,
    onDaySelected: (CalendarDay) -> Unit,
    onMonthChanged: (Long) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.inversePrimary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                if (isExpanded) "Esse mês" else "Essa semana",
                style = bodyLargeMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                if (isExpanded) "Ver menos" else "Ver mais",
                style = bodyMediumMd,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { isExpanded = !isExpanded }
            )
        }

        if (isExpanded) {
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onMonthChanged(-1) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Mês anterior",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                AnimatedContent(
                    targetState = month,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "monthLabel",
                    modifier = Modifier.weight(1f)
                ) { shownMonth ->
                    Text(
                        shownMonth.label(),
                        style = bodyMediumMd,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                IconButton(onClick = { onMonthChanged(1) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Próximo mês",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            weekDayLetters.forEach { letter ->
                Text(
                    letter,
                    style = bodyMediumMd,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        if (isExpanded) {
            var dragTotal by remember { mutableStateOf(0f) }
            AnimatedContent(
                targetState = month,
                transitionSpec = {
                    // Newer months enter from the right, older ones from the left
                    val direction = if (targetState > initialState) 1 else -1
                    (slideInHorizontally(tween(300)) { it * direction } + fadeIn(tween(300)))
                        .togetherWith(
                            slideOutHorizontally(tween(300)) { -it * direction } + fadeOut(tween(300))
                        )
                        .using(SizeTransform(clip = true))
                },
                label = "monthGrid",
                modifier = Modifier.pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { dragTotal = 0f },
                        onDragEnd = {
                            if (dragTotal > SWIPE_THRESHOLD) onMonthChanged(-1)
                            else if (dragTotal < -SWIPE_THRESHOLD) onMonthChanged(1)
                        },
                        onHorizontalDrag = { _, delta -> dragTotal += delta }
                    )
                }
            ) { shownMonth ->
                // Built per month so the outgoing month keeps its own days while sliding out
                val weeks = remember(shownMonth, selectedDate, reportDates) {
                    buildMonthWeeks(shownMonth, selectedDate, reportDates)
                }
                Column {
                    weeks.forEachIndexed { index, week ->
                        CalendarWeekRow(week = week, onDaySelected = onDaySelected)
                        if (index != weeks.lastIndex) {
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        } else {
            CalendarWeekRow(week = currentWeek, onDaySelected = onDaySelected)
        }
    }
}

@Composable
private fun CalendarWeekRow(
    week: List<CalendarDay?>,
    onDaySelected: (CalendarDay) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        week.forEach { day ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (day == null) {
                    Spacer(Modifier.size(36.dp))
                } else if (!day.isEnabled) {
                    DisabledDayCircle()
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (day.isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                            .then(
                                if (day.isSelected) Modifier else Modifier.border(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                    CircleShape
                                )
                            )
                            .clickable { onDaySelected(day) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            day.number,
                            style = bodySmallMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (day.isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DisabledDayCircle() {
    val stripeColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
    Canvas(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.Transparent)
    ) {
        if (size.width <= 0f || size.height <= 0f) return@Canvas
        val step = size.width / 4f
        if (step <= 0f) return@Canvas
        var x = -size.height
        var limit = 0
        while (x < size.width && limit < 50) {
            drawLine(
                color = stripeColor,
                start = Offset(x, size.height),
                end = Offset(x + size.height, 0f),
                strokeWidth = 3.dp.toPx()
            )
            x += step
            limit++
        }
    }
}

@Composable
private fun ReportFilterPanel(
    selectedTimeFilters: Set<ReportTimeFilter>,
    selectedStatusFilters: Set<ReportStatus>,
    onTimeFilterToggled: (ReportTimeFilter) -> Unit,
    onStatusFilterToggled: (ReportStatus) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        modifier = Modifier.width(220.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Filtrar por tempo",
                style = bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(Modifier.height(8.dp))
            ReportTimeFilter.values().forEach { filter ->
                FilterCheckboxRow(
                    label = filter.label,
                    checked = filter in selectedTimeFilters,
                    onCheckedChange = { onTimeFilterToggled(filter) }
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
            Spacer(Modifier.height(12.dp))

            Text(
                "Filtrar por categoria",
                style = bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(Modifier.height(8.dp))
            ReportStatus.values().forEach { status ->
                FilterCheckboxRow(
                    label = status.filterLabel,
                    checked = status in selectedStatusFilters,
                    onCheckedChange = { onStatusFilterToggled(status) }
                )
            }
        }
    }
}

@Composable
private fun FilterCheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
        )
        Text(label, style = bodySmall, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ReportHistoryScreenManagerPreview() {
    AetherTheme {
        ReportHistoryScreen(
            viewerRole = ReportViewerRole.GERENTE,
            currentWeek = sampleCurrentWeek,
            month = YearMonth.of(2026, 9),
            reportDates = sampleReportDates,
            selectedDate = LocalDate.of(2026, 9, 8),
            reportItems = listOf(
                ReportItem(
                    id = "1",
                    avatarUrl = null,
                    title = "Relatório sobre fulano the town",
                    authorName = "Daniel Sagaz",
                    reviewerName = "Nisflei Grandão",
                    createdAt = "24/07/2026",
                    status = ReportStatus.EM_ANALISE
                ),
                ReportItem(
                    id = "2",
                    avatarUrl = null,
                    title = "Relatório sobre fulano the town",
                    authorName = "Daniel Sagaz",
                    reviewerName = "Nisflei Grandão",
                    createdAt = "20/06/2026",
                    status = ReportStatus.APROVADO,
                    canReview = true
                )
            ),
            searchQuery = "",
            selectedTimeFilters = setOf(ReportTimeFilter.MAIS_RECENTES),
            selectedStatusFilters = setOf(ReportStatus.EM_ANALISE),
            isRefreshing = false,
            loadFailed = false,
            onRefresh = {},
            onSearchQueryChanged = {},
            onTimeFilterToggled = {},
            onStatusFilterToggled = {},
            onDaySelected = {},
            onMonthChanged = {},
            onReviewClick = {},
            onBackClicked = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ReportHistoryScreenEmployeePreview() {
    AetherTheme {
        ReportHistoryScreen(
            viewerRole = ReportViewerRole.FUNCIONARIO,
            currentWeek = sampleCurrentWeek,
            month = YearMonth.of(2026, 9),
            reportDates = sampleReportDates,
            selectedDate = LocalDate.of(2026, 9, 8),
            reportItems = listOf(
                ReportItem(
                    id = "1",
                    avatarUrl = null,
                    title = "Relatório sobre fulano the town",
                    authorName = "Daniel Sagaz",
                    reviewerName = "Nisflei Grandão",
                    createdAt = "24/07/2026",
                    status = ReportStatus.SOLICITADO
                )
            ),
            searchQuery = "",
            selectedTimeFilters = setOf(ReportTimeFilter.MAIS_RECENTES),
            selectedStatusFilters = setOf(ReportStatus.EM_ANALISE),
            isRefreshing = false,
            loadFailed = false,
            onRefresh = {},
            onSearchQueryChanged = {},
            onTimeFilterToggled = {},
            onStatusFilterToggled = {},
            onDaySelected = {},
            onMonthChanged = {},
            onReviewClick = {},
            onBackClicked = {}
        )
    }
}

private val sampleCurrentWeek = listOf(
    CalendarDay("05", isEnabled = false),
    CalendarDay("06"),
    CalendarDay("07"),
    CalendarDay("08", isSelected = true),
    CalendarDay("09"),
    CalendarDay("10"),
    CalendarDay("11", isEnabled = false)
)

private val sampleReportDates = (1..30).map { LocalDate.of(2026, 9, it) }.toSet()
