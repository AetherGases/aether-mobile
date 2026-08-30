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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.R
import com.aether.core.ui.components.AppHeader
import com.aether.core.ui.components.ManagerHomeHeroCard
import com.aether.core.ui.components.ReportsSummaryCard
import com.aether.core.ui.components.SealProgressCard
import com.aether.core.ui.components.UnitEmissionsCard
import com.aether.core.ui.theme.*

@Composable
fun ManagerHomeScreen(
    userName: String,
    userLastName: String,
    avatarUrl: String?,
    hasUnreadNotifications: Boolean,
    lastSubmittedLabel: String,
    totalEmissionsValue: String,
    reductionAchievedLabel: String,
    reviewedReportsCount: String,
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
                color = purple300.copy(alpha = 0.6f),
                radius = size.width * 0.55f,
                center = Offset(x = size.width * 0.1f, y = size.height * 0.08f)
            )
            drawCircle(
                color = green100.copy(alpha = 0.4f),
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
                        0 -> ManagerHomeHeroCard(
                            lastSubmittedLabel = lastSubmittedLabel,
                            totalEmissions = totalEmissionsValue,
                            reductionAchieved = reductionAchievedLabel
                        )
                        1 -> ReportsSummaryCard(
                            title = "Relatórios analisados",
                            lastSubmittedLabel = lastSubmittedLabel,
                            count = reviewedReportsCount,
                            onViewHistoryClick = onViewHistoryClick
                        )
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

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManagerHomeScreenPreview() {
    AetherTheme {
        ManagerHomeScreen(
            userName = "Nisflei",
            userLastName = "Grandão",
            avatarUrl = "https://i.pravatar.cc/150?img=33",
            hasUnreadNotifications = true,
            lastSubmittedLabel = "26/01/2026",
            totalEmissionsValue = "2.1M",
            reductionAchievedLabel = "8,3%",
            reviewedReportsCount = "450",
            unitEmissionsValue = "2.403",
            unitEmissionsChangeLabel = "12% ↗ em relação ao período anterior",
            sealLevelPercent = "50%",
            sealCriteriaLabel = "2 de 4 critérios atendidos",
            quickActions = listOf(
                QuickAction("Histórico", R.drawable.ic_history_purple, 60.dp, 3.dp, 0.dp, Color(0xFFECE2FD)) {},
                QuickAction("Calcular CO2", R.drawable.ic_calculator_green, 60.dp, 0.dp, 0.dp, Color(0xFFE7FAF2)) {},
                QuickAction("Chatbot", R.drawable.ic_aeko, 90.dp, 3.dp, 10.dp, Color(0xFFECE2FD)) {}
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
            onReportMenuClick = {}
        )
    }
}