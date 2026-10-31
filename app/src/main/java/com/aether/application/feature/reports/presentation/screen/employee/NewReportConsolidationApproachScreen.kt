package com.aether.application.feature.report.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.R
import com.aether.application.core.ui.components.FormErrorText
import com.aether.application.core.utils.UiText
import com.aether.application.core.utils.uiText
import com.aether.core.ui.theme.*

@Composable
fun ConsolidationApproachScreen(
    onBackClick: () -> Unit,
    onEquityShareClick: () -> Unit,
    onFinancialControlClick: () -> Unit,
    onOperationalControlClick: () -> Unit,
    onConsultAekoClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSubmitting: Boolean = false,
    errorMessage: UiText? = null
) {
    Box(modifier = modifier.fillMaxSize()) {
        ConsolidationApproachContent(
            onBackClick = onBackClick,
            onEquityShareClick = onEquityShareClick,
            onFinancialControlClick = onFinancialControlClick,
            onOperationalControlClick = onOperationalControlClick,
            onConsultAekoClick = onConsultAekoClick,
            errorMessage = errorMessage
        )

        if (isSubmitting) {
            // Swallows touches so an option can't be chosen twice while the report is created
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f))
                    .clickable(enabled = false, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun ConsolidationApproachContent(
    onBackClick: () -> Unit,
    onEquityShareClick: () -> Unit,
    onFinancialControlClick: () -> Unit,
    onOperationalControlClick: () -> Unit,
    onConsultAekoClick: () -> Unit,
    errorMessage: UiText?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        ambientColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                        spotColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    )
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(onClick = onBackClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_left),
                    contentDescription = stringResource(R.string.common_back),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = stringResource(R.string.reports_new_title),
                style = titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(R.string.reports_approach_title),
            style = titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.reports_approach_description),
            style = bodyLargeMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(48.dp))

        ConsolidationOptionItem(
            iconRes = R.drawable.ic_building,
            title = stringResource(R.string.reports_approach_equity_share),
            subtitle = stringResource(R.string.reports_approach_equity_share_subtitle),
            onClick = onEquityShareClick
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        ConsolidationOptionItem(
            iconRes = R.drawable.ic_coin,
            title = stringResource(R.string.reports_approach_financial_control),
            subtitle = stringResource(R.string.reports_approach_financial_control_subtitle),
            onClick = onFinancialControlClick
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        ConsolidationOptionItem(
            iconRes = R.drawable.ic_document,
            title = stringResource(R.string.reports_approach_operational_control),
            subtitle = stringResource(R.string.reports_approach_operational_control_subtitle),
            onClick = onOperationalControlClick
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            FormErrorText(errorMessage.asString())
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .clickable(onClick = onConsultAekoClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.outline)) {
                        append(stringResource(R.string.common_ask_aeko_help))
                    }
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(stringResource(R.string.common_ask_aeko_link))
                    }
                },
                style = bodyLargeMedium
            )
        }
    }
}

@Composable
private fun ConsolidationOptionItem(
    iconRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = titleMediumMD,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = bodyMediumMd,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 800)
@Composable
fun ConsolidationApproachScreenPreview() {
    AetherTheme {
        ConsolidationApproachScreen(
            onBackClick = {},
            onEquityShareClick = {},
            onFinancialControlClick = {},
            onOperationalControlClick = {},
            onConsultAekoClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 800)
@Composable
fun ConsolidationApproachScreenErrorPreview() {
    AetherTheme {
        ConsolidationApproachScreen(
            onBackClick = {},
            onEquityShareClick = {},
            onFinancialControlClick = {},
            onOperationalControlClick = {},
            onConsultAekoClick = {},
            errorMessage = uiText(R.string.reports_error_create_failed)
        )
    }
}