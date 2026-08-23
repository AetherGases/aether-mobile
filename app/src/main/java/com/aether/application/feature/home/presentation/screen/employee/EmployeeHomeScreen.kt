package com.aether.application.feature.home.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.R
import com.aether.core.ui.components.GlassIconButton
import com.aether.core.ui.theme.*

data class QuickAction(
    val label: String,
    val iconRes: Int,
    val backgroundColor: androidx.compose.ui.graphics.Color,
    val onClick: () -> Unit
)

data class RecentReport(
    val id: Int,
    val name: String,
    val ownerName: String,
    val createdAt: String,
    val status: String
)

@Composable
fun EmployeeHomeScreen(
    userName: String,
    userAvatarRes: Int,
    reportingPeriodLabel: String,
    submittedAtLabel: String,
    statusLabel: String,
    quickActions: List<QuickAction>,
    recentReports: List<RecentReport>,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSeeAllReportsClick: () -> Unit,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 1 })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(green100, purple100, backgroundLightElevated),
                    start = Offset(0f, 0f),
                    end = Offset(800f, 1200f)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = userAvatarRes),
                    contentDescription = userName,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "Seja bem-vindo,",
                        style = bodySmall,
                        color = textSecondaryLight
                    )
                    Text(
                        userName,
                        style = titleSmall,
                        color = textPrimaryLight
                    )
                }

                Spacer(Modifier.weight(1f))

                GlassIconButton(onClick = onNotificationsClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bell),
                        contentDescription = "Notificações",
                        tint = textPrimaryLight
                    )
                }
                Spacer(Modifier.width(8.dp))

                GlassIconButton(onClick = onSettingsClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "Configurações",
                        tint = textPrimaryLight
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Visão Geral",
                style = titleLarge,
                color = textPrimaryLight
            )

            Spacer(Modifier.height(16.dp))

            HorizontalPager(state = pagerState) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(purple500, purple300)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text("Último relatório", style = titleSmall, color = textPrimaryDark)
                        Text(
                            "Enviado em $submittedAtLabel",
                            style = bodySmall,
                            color = textPrimaryDark.copy(alpha = 0.8f)
                        )

                        Spacer(Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(purple300.copy(alpha = 0.3f))
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    "Período do relatório",
                                    style = bodySmall,
                                    color = textPrimaryDark.copy(alpha = 0.8f)
                                )

                                Text(
                                    reportingPeriodLabel,
                                    style = displayMedium,
                                    color = textPrimaryDark
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(lightYellow)
                            )
                            Spacer(Modifier.width(8.dp))

                            Text(
                                statusLabel,
                                style = bodySmall,
                                color = textPrimaryDark
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Ações Rápidas",
                style = titleLarge,
                color = textPrimaryLight
            )

            Spacer(Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(quickActions) { action ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { action.onClick() }
                            .width(80.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(action.backgroundColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = action.iconRes),
                                contentDescription = action.label,
                                tint = purple500
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            action.label,
                            style = bodySmall,
                            color = textPrimaryLight,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Últimos Relatórios", style = titleLarge, color = textPrimaryLight)
                Text(
                    "Ver tudo",
                    style = bodyMedium,
                    color = purple500,
                    modifier = Modifier.clickable { onSeeAllReportsClick() }
                )
            }

            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                recentReports.forEach { report ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(backgroundLightElevated)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = userAvatarRes),
                            contentDescription = report.ownerName,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(report.name, style = titleSmall, color = textPrimaryLight, maxLines = 2)
                            Text("Realizado por ${report.ownerName}", style = bodySmall, color = textSecondaryLight)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Feito em ${report.createdAt}", style = bodySmall, color = green500)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(purple100)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(report.status, style = labelSmall, color = purple700)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        GlassIconButton(
            onClick = onHomeClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bell),
                contentDescription = "Home",
                tint = green500
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeeHomeScreenPreview() {
    AetherTheme {
        EmployeeHomeScreen(
            userName = "Joilsson",
            userAvatarRes = 0,
            reportingPeriodLabel = "25/04/2030",
            submittedAtLabel = "25/19/1020",
            statusLabel = "Pendente",
            quickActions = listOf(
                QuickAction("Criar relatório", 0, green100) {},
                QuickAction("Calcular CO2", 0, purple100) {},
                QuickAction("Histórico", 0, green100) {}
            ),
            recentReports = listOf(
                RecentReport(
                    id = 1,
                    name = "Relatório sobre fulano the town",
                    ownerName = "você",
                    createdAt = "29/12/2025",
                    status = "Pendente"
                )
            ),
            onNotificationsClick = {},
            onSettingsClick = {},
            onSeeAllReportsClick = {},
            onHomeClick = {}
        )
    }
}