package com.aether.application.feature.notifications.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.core.ui.components.AetherTopBar
import com.aether.core.ui.components.NotificationCard
import com.aether.core.ui.components.NotificationStatus
import com.aether.core.ui.theme.*

enum class NotificationFilter(val label: String) {
    RECENTES("Mais recentes"),
    ANTIGAS("Mais antigas"),
    NAO_LIDAS("Não lidas")
}

data class NotificationItem(
    val status: NotificationStatus,
    val title: String,
    val reviewer: String,
    val timestamp: String,
    val relativeTime: String
)

data class NotificationSection(
    val title: String,
    val items: List<NotificationItem>
)

@Composable
fun NotificationsScreen(
    sections: List<NotificationSection>,
    selectedFilter: NotificationFilter,
    onFilterSelected: (NotificationFilter) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AetherTopBar(title = "Notificações", onBackClick = onBackClicked)

        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 4.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NotificationFilter.values().forEach { filter ->
                NotificationFilterChip(
                    label = filter.label,
                    selected = filter == selectedFilter,
                    onClick = {onFilterSelected(filter)}
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            sections.forEach {section ->
                Spacer(Modifier.height(36.dp))
                Text(section.title, style = titleLarge, color = textPrimaryLight)
                Spacer(Modifier.height(14.dp))
                section.items.forEachIndexed { index, item ->
                    NotificationCard(
                        status = item.status,
                        title = item.title,
                        reviewer = item.reviewer,
                        timestamp = item.timestamp,
                        relativeTime = item.relativeTime,
                        modifier = Modifier.shadow(elevation = 12.dp, shape = RoundedCornerShape(18.dp), ambientColor = textTertiaryLight.copy(alpha = 0.5f), spotColor = textTertiaryLight.copy(alpha = 0.5f))

                    )
                    if (index != section.items.lastIndex) {
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
            Spacer(Modifier.height(36.dp))
        }
    }
}

@Composable
private fun NotificationFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .then(
                if (selected) Modifier.shadow(elevation = 6.dp, shape = shape, ambientColor = purple500, spotColor = purple500)
                else Modifier
            )
            .clip(shape)
            .then(
                if (selected) {
                    Modifier
                        .background(Brush.horizontalGradient(listOf(Color(0xFFA27EF3), purple500)), shape)
                } else {
                    Modifier
                        .background(green500.copy(alpha = 0.1f), shape)
                        .border(1.dp, green500.copy(alpha = 0.35f), shape)
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            style = labelLarge,
            maxLines = 1,
            softWrap = false,
            color = if (selected) Color.White else green500
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun NotificationsScreenPreview() {
    AetherTheme {
        NotificationsScreen(
            sections = listOf(
                NotificationSection(
                    title = "Hoje",
                    items = listOf(
                        NotificationItem(NotificationStatus.APROVADO, "Relatório aprovado", "Revisado por Nisflei Grandão", "Hoje 09:12", "Há 2 horas"),
                        NotificationItem(NotificationStatus.REPROVADO, "Relatório reprovado", "Revisado por Nisflei Grandão", "Hoje 09:12", "Há 2 horas")
                    )
                ),
                NotificationSection(
                    title = "Essa semana",
                    items = listOf(
                        NotificationItem(NotificationStatus.APROVADO, "Relatório aprovado", "Revisado por Nisflei Grandão", "Hoje 09:12", "Há 2 horas"),
                        NotificationItem(NotificationStatus.APROVADO, "Relatório aprovado", "Revisado por Nisflei Grandão", "Hoje 09:12", "Há 2 horas"),
                        NotificationItem(NotificationStatus.REPROVADO, "Relatório reprovado", "Revisado por Nisflei Grandão", "Hoje 09:12", "Há 2 horas")
                    )
                ),
                NotificationSection(
                    title = "Esse mês",
                    items = listOf(
                        NotificationItem(NotificationStatus.REPROVADO, "Relatório reprovado", "Revisado por Nisflei Grandão", "Hoje 09:12", "Há 2 horas"),
                        NotificationItem(NotificationStatus.REPROVADO, "Relatório reprovado", "Revisado por Nisflei Grandão", "Hoje 09:12", "Há 2 horas"),
                        NotificationItem(NotificationStatus.APROVADO, "Relatório aprovado", "Revisado por Nisflei Grandão", "Hoje 09:12", "Há 2 horas")
                    )
                )
            ),
            selectedFilter = NotificationFilter.RECENTES,
            onFilterSelected = {},
            onBackClicked = {}
        )
    }
}