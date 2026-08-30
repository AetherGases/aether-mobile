package com.aether.application.feature.home.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aether.application.R
import com.aether.core.ui.components.AppHeader
import com.aether.core.ui.components.LastReportHeroCard
import com.aether.core.ui.components.ReportsSummaryCard
import com.aether.core.ui.components.SealProgressCard
import com.aether.core.ui.components.UnitEmissionsCard
import com.aether.core.ui.theme.*

data class QuickAction(
    val label: String,
    val iconRes: Int,
    val iconSize: Dp = 28.dp,
    val offsetX: Dp = 0.dp,
    val offsetY: Dp = 0.dp,
    val backgroundColor: Color,
    val onClick: () -> Unit
)

enum class ReportStatus(
    val label: String,
    val color: Color,
    val backgroundColor: Color,
    val iconRes: Int
){
    PENDENTE("Pendente", Color(0xFF7848C5), Color(0xFFECE7FE), R.drawable.ic_pendent),
    APROVADO("Aprovado", Color(0xFF498371), Color(0xFFD6F6E8), R.drawable.ic_approved),
    RECUSADO("Recusado", lightRed, Color(0xFFF9C5CC), R.drawable.ic_rejected)
}

data class RecentReport(
    val id: Int,
    val name: String,
    val ownerName: String,
    val ownerAvatarUrl: String?,
    val createdAt: String,
    val status: ReportStatus
)

@Composable
fun EmployeeHomeScreen(
    userName: String,
    userLastName: String,
    avatarUrl: String?,
    hasUnreadNotifications: Boolean,
    lastSubmittedLabel: String,
    reportingPeriodLabel: String,
    reportStatusLabel: String,
    reportsCount: String,
    unitEmissionsValue: String,
    unitEmissionsChangeLabel: String,
    sealLevelPercent: String,
    sealCriteriaLabel: String,
    quickActions: List<QuickAction>,
    recentReports: List<RecentReport>,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onViewHistoryClick: () -> Unit,
    onSeeAllReportsClick: () -> Unit,
    onReportMenuClick: (RecentReport) -> Unit,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundLightElevated)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(90.dp)
        ) {
            drawCircle(
                color = Color(0xFFECE2FD),
                radius = size.width * 0.55f,
                center = Offset(x = size.width * 0.1f, y = size.height * 0.08f)
            )
            drawCircle(
                color = Color(0xFFE7FAF2),
                radius = size.width * 0.5f,
                center = Offset(x = size.width * 0.9f, y = size.height * 0.05f)
            )
            drawCircle(
                color = backgroundLightElevated,
                radius = size.width * 0.6f,
                center = Offset(x = size.width * 0.4f, y = size.height * 0.45f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            AppHeader(
                userName = "$userName $userLastName",
                avatarUrl = avatarUrl,
                hasUnreadNotifications = hasUnreadNotifications,
                onNotificationsClick = onNotificationsClick,
                onSettingsClick = onSettingsClick
            )

            Spacer(Modifier.height(24.dp))

            Text("Visão Geral", style = titleLarge, color = textPrimaryLight)
            Spacer(Modifier.height(16.dp))

            val pagerState = rememberPagerState(pageCount = { 4 })
            HorizontalPager(state = pagerState) { page ->
                Box(
                    modifier = Modifier.shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = purple300.copy(alpha = 0.4f),
                        spotColor = purple300.copy(alpha = 0.4f)
                    )
                ) {
                    when (page) {
                        0 -> LastReportHeroCard(lastSubmittedLabel, reportingPeriodLabel, reportStatusLabel)
                        1 -> ReportsSummaryCard("Relatórios realizados", lastSubmittedLabel, reportsCount, onViewHistoryClick)
                        2 -> UnitEmissionsCard(unitEmissionsValue, "tCO₂e/mês", unitEmissionsChangeLabel)
                        3 -> SealProgressCard(sealLevelPercent, sealCriteriaLabel)
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (index == pagerState.currentPage) green500 else Color(0xFFD6D9E8))
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Ações Rápidas", style = titleLarge, color = textPrimaryLight)
            Spacer(Modifier.height(16.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(quickActions) { action ->
                    QuickActionItem(action = action)
                }
            }

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Últimos Relatórios", style = titleLarge, color = textPrimaryLight)
                Text(
                    "Ver tudo",
                    style = titleSmall,
                    color = textTertiaryLight,
                    modifier = Modifier.clickable { onSeeAllReportsClick() }
                )
            }

            Spacer(Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                recentReports.forEach { report ->
                    RecentReportItem(report = report, onMenuClick = { onReportMenuClick(report) })
                }
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
fun QuickActionItem(action: QuickAction, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(100.dp)
            .clickable { action.onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(action.backgroundColor, RoundedCornerShape(16.dp))
,            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = if (action.iconRes != 0) action.iconRes else R.drawable.ic_home),
                contentDescription = action.label,
                modifier = Modifier
                    .size(action.iconSize)
                    .offset(x = action.offsetX, y = action.offsetY)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = action.label,
            style = bodyMediumMd,
            color = textPrimaryLight,
            maxLines = 1
        )
    }
}
@Composable
fun RecentReportItem(
    report: RecentReport,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = textTertiaryLight.copy(alpha = 0.15f),
                spotColor = textTertiaryLight.copy(alpha = 0.15f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundLightElevated)
            .border(
                width = 0.5.dp,
                color = textDisabled.copy(alpha = 0.4f),
                shape = RoundedCornerShape(20.dp) 
            )
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = report.ownerAvatarUrl,
            contentDescription = "Avatar de ${report.ownerName}",
            placeholder = painterResource(R.drawable.ic_avatar),
            error = painterResource(R.drawable.ic_avatar),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.LightGray)
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(

                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = report.name,
                    style = titleSmall,
                    color = textPrimaryLight,
                    maxLines = 2,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Mais opções",
                        tint = textTertiaryLight.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Realizado por ${report.ownerName}",
                style = bodySmall,
                color = textSecondaryLight
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = green500,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Feito em ${report.createdAt}",
                    style = bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = green500
                )

                Spacer(Modifier.weight(1f))

                StatusBadge(status = report.status)
            }
        }
    }
}

@Composable
fun StatusBadge(status: ReportStatus) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(status.backgroundColor)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = status.iconRes),
            contentDescription = null,
            tint = status.color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = status.label,
            style = labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = status.color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeeHomeScreenPreview() {
    AetherTheme {
        EmployeeHomeScreen(
            userName = "Daniel",
            userLastName = "Sagaz",
            avatarUrl = "https://i.pravatar.cc/150?img=12",
            hasUnreadNotifications = true,
            lastSubmittedLabel = "29/05/2026",
            reportingPeriodLabel = "Jan–Mar 2026",
            reportStatusLabel = "Aguardando revisão há 2 dias",
            reportsCount = "506",
            unitEmissionsValue = "2.403",
            unitEmissionsChangeLabel = "12% ↗ em relação ao período anterior",
            sealLevelPercent = "50%",
            sealCriteriaLabel = "2 de 4 critérios atendidos",
            quickActions = listOf(
                QuickAction("Criar relatório", R.drawable.ic_create_report, 60.dp,0.dp, 0.dp,Color(0xFFE7FAF2)) {},
                QuickAction("Calcular CO2", R.drawable.ic_calculator_purple, 50.dp,0.dp, 0.dp,Color(0xFFECE2FD)) {},
                QuickAction("Histórico", R.drawable.ic_history_green, 60.dp,3.dp, 0.dp,Color(0xFFE7FAF2)) {},
                QuickAction("Chatbot", R.drawable.ic_aeko, 90.dp, 3.dp, 10.dp,Color(0xFFECE2FD)) {}


                ),
            recentReports = listOf(
                RecentReport(1, "Relatório fulano town", "Daniel Sagaz", null, "29/12/2025", ReportStatus.PENDENTE),
                RecentReport(2, "Relatório fulano town", "Bruno Maldades", null, "29/12/2025", ReportStatus.APROVADO),
                RecentReport(3, "Relatório fulano town", "Camila Bezerra", null, "29/12/2025", ReportStatus.RECUSADO)
            ),
            onNotificationsClick = {},
            onSettingsClick = {},
            onViewHistoryClick = {},
            onSeeAllReportsClick = {},
            onReportMenuClick = {},
            onHomeClick = {}
        )
    }
}