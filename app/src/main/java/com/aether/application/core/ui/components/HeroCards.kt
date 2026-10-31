package com.aether.core.ui.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.R
import com.aether.application.core.utils.UiText
import com.aether.core.ui.theme.*

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
                    stringResource(R.string.herocard_last_sent_on, lastSubmittedLabel),
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
                    stringResource(R.string.herocard_this_month),
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
                Text(stringResource(R.string.herocard_view_history), style = titleSmall, color = MaterialTheme.colorScheme.onSecondary)
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
                Text(stringResource(R.string.herocard_unit_emissions), style = titleSmall, color = MaterialTheme.colorScheme.onSurface)
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
                Text(stringResource(R.string.herocard_seal_title), style = titleSmall, color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(Modifier.height(12.dp))

            Text(stringResource(R.string.herocard_level), style = bodyMediumMd, color = MaterialTheme.colorScheme.outline)
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
    val title: UiText,
    val lastSubmittedLabel: String,
    val count: String,
    val onViewHistoryClick: () -> Unit,
) : HomeHeroCard {
    @Composable
    override fun Render() = ReportsSummaryCard(title.asString(), lastSubmittedLabel, count, onViewHistoryClick)
}

data class UnitEmissionsHomeCard(
    val value: String,
    val unitLabel: UiText,
    val changeLabel: UiText,
) : HomeHeroCard {
    @Composable
    override fun Render() = UnitEmissionsCard(value, unitLabel.asString(), changeLabel.asString())
}

data class SealProgressHomeCard(
    val levelPercent: String,
    val criteriaLabel: UiText,
) : HomeHeroCard {
    @Composable
    override fun Render() = SealProgressCard(levelPercent, criteriaLabel.asString())
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