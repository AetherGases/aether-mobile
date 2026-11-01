package com.aether.application.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.R
import com.aether.application.core.ui.theme.*

@Composable
fun ReportsSummaryCard(
    title: String,
    lastSubmittedLabel: String,
    count: String,
    onViewHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .then(modifier)
            .width(335.dp)
            .height(205.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.semantics(mergeDescendants = true) {}) {
                Text(title, style = titleSmall, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    "Último enviado em $lastSubmittedLabel",
                    style = bodySmallMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Row(
                modifier = Modifier.semantics(mergeDescendants = true) {},
                verticalAlignment = Alignment.Bottom
            ) {
                Text(count, style = displayLargeBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.width(6.dp))
                Text(
                    "Este mês",
                    style = bodyLargeMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.offset(y = (-24).dp)
                )
            }

            Button(
                onClick = onViewHistoryClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Ver histórico", style = titleSmall, color = MaterialTheme.colorScheme.onSecondary)
            }
        }

        Image(
            painter = painterResource(id = R.drawable.ic_report_illustration),
            contentDescription = null,
            modifier = Modifier
                .size(95.dp)
                .align(Alignment.TopEnd)
        )
    }
}

@Composable
fun UnitEmissionsCard(
    value: String,
    unitLabel: String,
    changeLabel: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .then(modifier)
            .width(335.dp)
            .height(205.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
            .focusable()
            .semantics(mergeDescendants = true) {}
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Emissões da unidade", style = titleSmall, color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(Modifier.height(12.dp))

            Column {
                Text(value, style = displayLargeBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.width(6.dp))
                Text(unitLabel, style = bodyLargeMedium, color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)

            ) {
                Text(changeLabel, style = labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f))
            }
        }
        Image(
            painter = painterResource(id = R.drawable.ic_factory_illustration),
            contentDescription = null,
            modifier = Modifier.size(125.dp)
                .align(Alignment.TopEnd)
        )
    }
}

@Composable
fun SealProgressCard(
    levelPercent: String,
    criteriaLabel: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .then(modifier)
            .width(335.dp)
            .height(205.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
            .focusable()
            .semantics(mergeDescendants = true) {}
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Selo Clima Paraná", style = titleSmall, color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(Modifier.height(12.dp))

            Text("Nível", style = bodyMediumMd, color = MaterialTheme.colorScheme.outline)
            Text(levelPercent, style = displayLargeBold, color = MaterialTheme.colorScheme.onSurface)

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_warning),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(criteriaLabel, style = bodyMediumMd, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Image(
            painter = painterResource(id = R.drawable.ic_seal_illustration),
            contentDescription = null,
            modifier = Modifier.size(110.dp)
                .align(Alignment.TopEnd)
                .offset(y = 20.dp)
        )
    }
}

data class ReportsSummaryHomeCard(
    val title: String,
    val lastSubmittedLabel: String,
    val count: String,
    val onViewHistoryClick: () -> Unit,
) : HomeHeroCard {
    @Composable
    override fun Render(modifier: Modifier) =
        ReportsSummaryCard(
            title = title,
            lastSubmittedLabel = lastSubmittedLabel,
            count = count,
            onViewHistoryClick = onViewHistoryClick,
            modifier = modifier
        )
}

data class UnitEmissionsHomeCard(
    val value: String,
    val unitLabel: String,
    val changeLabel: String,
) : HomeHeroCard {
    @Composable
    override fun Render(modifier: Modifier) =
        UnitEmissionsCard(
            value = value,
            unitLabel = unitLabel,
            changeLabel = changeLabel,
            modifier = modifier
        )
}

data class SealProgressHomeCard(
    val levelPercent: String,
    val criteriaLabel: String,
) : HomeHeroCard {
    @Composable
    override fun Render(modifier: Modifier) =
        SealProgressCard(
            levelPercent = levelPercent,
            criteriaLabel = criteriaLabel,
            modifier = modifier
        )
}

@Preview(showBackground = true)
@Composable
fun ReportsSummaryCardPreview() {
    AetherTheme {
        ReportsSummaryCard(
            title = "Relatórios realizados",
            lastSubmittedLabel = "26/03/2026",
            count = "506",
            onViewHistoryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UnitEmissionsCardPreview() {
    AetherTheme {
        UnitEmissionsCard(
            value = "2.403",
            unitLabel = "tCO₂e/mês",
            changeLabel = "12% ↗ em relação ao período anterior"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SealProgressCardPreview() {
    AetherTheme {
        SealProgressCard(
            levelPercent = "50%",
            criteriaLabel = "2 de 4 critérios atendidos"
        )
    }
}