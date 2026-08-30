package com.aether.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.HazeState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.R
import com.aether.core.ui.theme.*

@Composable
fun LastReportHeroCard(
    lastSubmittedLabel: String,
    reportingPeriodLabel: String,
    statusLabel: String,
    modifier: Modifier = Modifier
) {
    val hazeState = remember { HazeState() }

    Box(
        modifier = modifier
            .width(335.dp)
            .height(205.dp)
            .clip(RoundedCornerShape(18.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_hero_card_purple),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .haze(hazeState)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Último relatório", style = titleSmall, color = textPrimaryDark)
                Text(
                    "Enviado em $lastSubmittedLabel",
                    style = bodySmallMedium,
                    color = textPrimaryDark
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .hazeChild(hazeState) {
                        blurRadius = 20.dp
                    }
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                backgroundLightElevated.copy(alpha = 0.8f),
                                backgroundLightElevated.copy(alpha = 0.3f),
                                backgroundLightElevated
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        "Período do relatório",
                        style = bodySmallMedium,
                        color = textPrimaryDark
                    )
                    Text(reportingPeriodLabel, style = displayMedium, color = textPrimaryDark)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(lightYellow)
                )
                Spacer(Modifier.width(8.dp))
                Text(statusLabel, style = bodySmall, color = textPrimaryDark)
            }
        }
    }
}

// Só teste pra visualizar
@Preview(showBackground = true)
@Composable
fun LastReportHeroCardPreview() {
    AetherTheme {
        LastReportHeroCard(
            lastSubmittedLabel = "29/05/2026",
            reportingPeriodLabel = "Jan–Mar 2026",
            statusLabel = "Aguardando revisão há 2 dias"
        )
    }
}