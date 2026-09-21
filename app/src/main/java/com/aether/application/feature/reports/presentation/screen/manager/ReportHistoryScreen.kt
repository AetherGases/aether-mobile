package com.aether.application.feature.reports.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.aether.core.ui.components.AetherTopBar
import com.aether.core.ui.components.ReportCard
import com.aether.core.ui.components.ReportStatus
import com.aether.core.ui.theme.*

enum class ReportViewerRole {GERENTE, FUNCIONARIO}

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
    val reviewerName: String,
    val createdAt: String,
    val status: ReportStatus,
    val canReview: Boolean = false
)

data class CalendarDay(
    val number: String,
    val isSelected: Boolean = false,
    val isEnabled: Boolean = true
)

private val weekDayLetters = listOf("D", "S", "T", "Q", "Q", "S", "S")

@Composable
fun ReportHistoryScreen(
    viewerRole: ReportViewerRole,
    currentWeek: List<CalendarDay?>,
    monthWeeks: List<List<CalendarDay?>>,
    reportItems: List<ReportItem>,
    searchQuery: String,
    selectedTimeFilters: Set<ReportTimeFilter>,
    selectedStatusFilters: Set<ReportStatus>,
    onSearchQueryChanged: (String) -> Unit,
    onTimeFilterToggled: (ReportTimeFilter) -> Unit,
    onStatusFilterToggled: (ReportStatus) -> Unit,
    onDaySelected: (CalendarDay) -> Unit,
    onReviewClick: (String) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var filterPanelVisible by remember {mutableStateOf(false)}

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AetherTopBar(title = "Gerenciamento de relatórios", onBackClick = onBackClicked)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            Text("Histórico", style = titleLarge, color = textPrimaryLight)

            Spacer(Modifier.height(16.dp))

            ReportCalendarCard(
                currentWeek = currentWeek,
                monthWeeks = monthWeeks,
                onDaySelected = onDaySelected
            )

            Spacer(Modifier.height(20.dp))

            Box {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        placeholder = {Text("Procurar relatório")},
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_search),
                                contentDescription = null,
                                tint = textTertiaryLight
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default,
                        shape = RoundedCornerShape(percent = 50),

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = purple300,
                            unfocusedBorderColor = textDisabledLight.copy(alpha = 0.25f),
                            unfocusedContainerColor = backgroundLight,
                            focusedContainerColor = Color(0xFFD6DCEA)
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
                                    colors = listOf(green500.copy(alpha = 0.5f), green100)
                                )
                            )
                            .clickable {filterPanelVisible = !filterPanelVisible },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_tune),
                            contentDescription = "Filtrar",
                            tint = green700,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (filterPanelVisible && !LocalInspectionMode.current) {
                    val offsetY = with(LocalDensity.current) { 60.dp.roundToPx() }
                    Popup(
                        alignment = Alignment.TopEnd,
                        offset = IntOffset(x = 0, y = offsetY),
                        onDismissRequest = {filterPanelVisible = false }
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

            reportItems.forEachIndexed {index, item ->
                val secondaryLabel = when (viewerRole) {
                    ReportViewerRole.GERENTE -> "Realizado por ${item.authorName}"
                    ReportViewerRole.FUNCIONARIO -> "Revisado por ${item.reviewerName}"
                }

                ReportCard(
                    avatarUrl = item.avatarUrl,
                    title = item.title,
                    secondaryLabel = secondaryLabel,
                    createdAt = item.createdAt,
                    status = item.status,
                    onReviewClick = if (item.canReview) {
                        {onReviewClick(item.id)}
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

@Composable
private fun ReportCalendarCard(
    currentWeek: List<CalendarDay?>,
    monthWeeks: List<List<CalendarDay?>>,
    onDaySelected: (CalendarDay) -> Unit
) {
    var isExpanded by remember {mutableStateOf(false)}

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        purple300,
                        purple100
                    )
                )
            )
            .border(1.dp, purple500, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                if (isExpanded) "Esse mês" else "Essa semana",
                style = bodyLargeMedium,
                color = textPrimaryLight
            )
            Text(
                if (isExpanded) "Ver menos" else "Ver mais",
                style = bodyMediumMd,
                color = purple500,
                modifier = Modifier.clickable {isExpanded = !isExpanded}
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            weekDayLetters.forEach {letter ->
                Text(
                    letter,
                    style = bodyMediumMd,
                    color = textSecondaryLight,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        if (isExpanded) {
            monthWeeks.forEachIndexed { index, week ->
                CalendarWeekRow(week = week, onDaySelected = onDaySelected)
                if (index != monthWeeks.lastIndex) {
                    Spacer(Modifier.height(8.dp))
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
                            .background(if (day.isSelected) purple500 else Color.White)
                            .then(
                                if (day.isSelected) Modifier else Modifier.border(1.dp, purple500.copy(alpha = 0.8f), CircleShape)
                            )
                            .clickable {onDaySelected(day) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            day.number,
                            style = bodySmallMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (day.isSelected) Color.White else textSecondaryLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DisabledDayCircle() {
    Canvas(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.White)
    ) {
        if (size.width <= 0f || size.height <= 0f) return@Canvas
        val stripeColor = green500.copy(alpha = 0.4f)
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
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.width(220.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Filtrar por tempo",
                style = bodySmall,
                fontWeight = FontWeight.Bold,
                color = textTertiaryLight
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
            androidx.compose.material3.HorizontalDivider(color = green500.copy(alpha = 0.4f))
            Spacer(Modifier.height(12.dp))

            Text(
                "Filtrar por categoria",
                style = bodySmall,
                fontWeight = FontWeight.Bold,
                color = textTertiaryLight
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
            colors = CheckboxDefaults.colors(checkedColor = purple300)
        )
        Text(label, style = bodySmall, color = textPrimaryLight)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ReportHistoryScreenManagerPreview() {
    AetherTheme {
        ReportHistoryScreen(
            viewerRole = ReportViewerRole.GERENTE,
            currentWeek = sampleCurrentWeek,
            monthWeeks = sampleMonthWeeks,
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
            onSearchQueryChanged = {},
            onTimeFilterToggled = {},
            onStatusFilterToggled = {},
            onDaySelected = {},
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
            monthWeeks = sampleMonthWeeks,
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
            onSearchQueryChanged = {},
            onTimeFilterToggled = {},
            onStatusFilterToggled = {},
            onDaySelected = {},
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

private val sampleMonthWeeks = listOf(
    listOf(null, null, null, CalendarDay("01"), CalendarDay("02"), CalendarDay("03"), CalendarDay("04", isEnabled = false)),
    listOf(
        CalendarDay("05", isEnabled = false), CalendarDay("06"), CalendarDay("07"),
        CalendarDay("08", isSelected = true), CalendarDay("09"), CalendarDay("10"), CalendarDay("11", isEnabled = false)
    ),
    listOf(
        CalendarDay("12", isEnabled = false), CalendarDay("13"), CalendarDay("14"),
        CalendarDay("15"), CalendarDay("16"), CalendarDay("17"), CalendarDay("18", isEnabled = false)
    ),
    listOf(
        CalendarDay("19", isEnabled = false), CalendarDay("20"), CalendarDay("21"),
        CalendarDay("22"), CalendarDay("23"), CalendarDay("24"), CalendarDay("25", isEnabled = false)
    ),
    listOf(
        CalendarDay("26", isEnabled = false), CalendarDay("27"), CalendarDay("28"),
        CalendarDay("29"), CalendarDay("30"), null, null
    )
)