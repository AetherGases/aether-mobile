package com.aether.application.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aether.application.R
import com.aether.application.core.ui.theme.*

enum class ReportStatus(val label: String) {
    EM_ANALISE("Em análise"),
    APROVADO("Aprovado"),
    REJEITADO("Rejeitado"),
    EMITIDO("Emitido"),
    SOLICITADO("Solicitado")
}

@Composable
private fun statusColor(status: ReportStatus): Color = when (status) {
    ReportStatus.EM_ANALISE -> MaterialTheme.colorScheme.primary
    ReportStatus.APROVADO -> MaterialTheme.colorScheme.secondary
    ReportStatus.REJEITADO -> MaterialTheme.colorScheme.error
    ReportStatus.EMITIDO -> MaterialTheme.colorScheme.tertiary
    ReportStatus.SOLICITADO -> MaterialTheme.colorScheme.outline
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
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                placeholder = painterResource(R.drawable.ic_avatar),
                error = painterResource(R.drawable.ic_avatar),
                // A null url (an author without a photo) is neither a load nor an error
                fallback = painterResource(R.drawable.ic_avatar),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
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
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    StatusBadge(text = status.label, color = statusColor(status))
                }
                Spacer(Modifier.height(4.dp))
                Text(secondaryLabel, style = bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Data de criação: $createdAt",
                    style = bodySmallMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (onReviewClick != null) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.inversePrimary,
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        )
                        .clickable(onClick = onReviewClick)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Revisar", style = bodyMediumMd, color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
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