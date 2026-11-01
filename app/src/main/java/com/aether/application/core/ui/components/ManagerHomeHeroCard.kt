package com.aether.application.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.R
import com.aether.application.core.ui.theme.*

data class ManagerHeroCard(
    val lastSubmittedLabel: String,
    val totalEmissions: String,
    val reductionAchieved: String,
    val summaryCount: String
) : HomeHeroCard {
    @Composable
    override fun Render(modifier: Modifier) {
        Box(
            modifier = Modifier
                .then(modifier)
                .width(335.dp)
                .height(205.dp)
                .clip(RoundedCornerShape(18.dp))
                .focusable()
                .semantics(mergeDescendants = true) {}
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg_hero_card_purple),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Total de emissões mapeadas",
                        style = titleSmall,
                        color = Color.White
                    )
                    Text(
                        "Último envio em $lastSubmittedLabel",
                        style = bodySmallMedium,
                        color = Color.White
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
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                                        MaterialTheme.colorScheme.surface
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
                                color = Color.White,
                                maxLines = 1
                            )
                            Text(totalEmissions, style = displayMedium, color = Color.White)
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
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                                        MaterialTheme.colorScheme.surface
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
                                color = Color.White,
                                maxLines = 1
                            )
                            Text(reductionAchieved, style = displayMedium, color = Color.White)
                        }
                    }
                }

                Spacer(Modifier.height(0.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManagerHomeHeroCardPreview() {
    AetherTheme {
        ManagerHeroCard(
            lastSubmittedLabel = "26/01/2026",
            totalEmissions = "2.1M",
            reductionAchieved = "8,3%",
            summaryCount = "450"
        ).Render()
    }
}