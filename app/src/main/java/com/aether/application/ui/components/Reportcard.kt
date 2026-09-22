package com.aether.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aether.application.R
import com.aether.core.ui.theme.*

enum class ReportStatus(val label: String) {
    EM_ANALISE("Em análise"),
    APROVADO("Aprovado"),
    REJEITADO("Rejeitado"),
    EMITIDO("Emitido"),
    SOLICITADO("Solicitado")
}

private fun statusColor(status: ReportStatus): Color = when (status) {
    ReportStatus.EM_ANALISE -> purple500
    ReportStatus.APROVADO -> green500
    ReportStatus.REJEITADO -> lightRed
    ReportStatus.EMITIDO -> lightYellow
    ReportStatus.SOLICITADO -> textTertiaryLight
}

@Composable
fun ReportCard(
    avatarUrl: String?,
    title: String,
    secondaryLabel: String,
    createdAt: String,
    status: ReportStatus,
    onReviewClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, textDisabledLight.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        title,
                        style = labelLarge,
                        color = textPrimaryLight,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    StatusBadge(text = status.label, color = statusColor(status))
                }
                Spacer(Modifier.height(4.dp))
                Text(secondaryLabel, style = bodySmall, color = textTertiaryLight)
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.calendarmonth),
                    contentDescription = null,
                    tint = textTertiaryLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Data de criação: $createdAt",
                    style = bodySmallMedium,
                    color = textTertiaryLight
                )
            }

            if (onReviewClick != null) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Brush.linearGradient(listOf(Color(0xFF8A47FD), purple500)))
                        .clickable(onClick = onReviewClick)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Revisar", style = bodyMediumMd, color = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, style = labelSmall, color = color)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F7)
@Composable
fun ReportCardPreview() {
    AetherTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReportCard(
                avatarUrl = null,
                title = "Relatório sobre fulano the town",
                secondaryLabel = "Realizado por Daniel Sagaz",
                createdAt = "24/07/2026",
                status = ReportStatus.EM_ANALISE,
                onReviewClick = {}
            )
            ReportCard(
                avatarUrl = null,
                title = "Relatório sobre fulano the town",
                secondaryLabel = "Realizado por Daniel Sagaz",
                createdAt = "20/06/2026",
                status = ReportStatus.APROVADO,
                onReviewClick = {}
            )
        }
    }
}