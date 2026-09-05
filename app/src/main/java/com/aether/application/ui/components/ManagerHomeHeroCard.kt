package com.aether.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

@Composable
fun ManagerHomeHeroCard(
    lastSubmittedLabel: String,
    totalEmissions: String,
    reductionAchieved: String,
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
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Total de emissões mapeadas", style = titleSmall, color = textPrimaryDark)
                Text(
                    "Último envio em $lastSubmittedLabel",
                    style = bodySmallMedium,
                    color = textPrimaryDark
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
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
                            "Total (em tCO₂e)",
                            style = bodySmallMedium,
                            color = textPrimaryDark,
                            maxLines = 1
                        )
                        Text(totalEmissions, style = displayMedium, color = textPrimaryDark)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
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
                            "Redução obtida",
                            style = bodySmallMedium,
                            color = textPrimaryDark,
                            maxLines = 1
                        )
                        Text(reductionAchieved, style = displayMedium, color = textPrimaryDark)
                    }
                }
            }

            Spacer(Modifier.height(0.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManagerHomeHeroCardPreview() {
    AetherTheme {
        ManagerHomeHeroCard(
            lastSubmittedLabel = "26/01/2026",
            totalEmissions = "2.1M",
            reductionAchieved = "8,3%"
        )
    }
}