package com.aether.application.feature.reports.presentation.screen.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.aether.application.R
import com.aether.application.feature.reports.presentation.components.ReportLoadState
import com.aether.application.core.ui.components.AetherTopBar
import com.aether.core.ui.theme.*

data class ReportReviewUi(
    val name: String,
    val sector: String?,
    val unitName: String,
    val submittedAt: String,
    val description: String?,
    /** Null while the report has not been reviewed yet. */
    val reviewerName: String?,
    val reviewerAvatarUrl: String?,
    val isApproved: Boolean,
    val validationMessage: String?,
    val scopes: List<ScopeGasesUi>,
    val canProceedToAnalysis: Boolean
)

data class ScopeGasesUi(
    val scope: String,
    val gases: List<GasEmissionUi>
)

data class GasEmissionUi(
    val name: String,
    val formula: String,
    val quantity: String
)

@Composable
fun ReportReviewScreen(
    report: ReportReviewUi?,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onBackClick: () -> Unit,
    onProceedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .drawBehind {
                val purpleRadius = size.width
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(purple500.copy(alpha = 0.18f), Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = purpleRadius
                    ),
                    radius = purpleRadius,
                    center = Offset(0f, 0f)
                )

                val greenRadius = size.width * 0.9f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(green500.copy(alpha = 0.18f), Color.Transparent),
                        center = Offset(size.width, size.height * 0.1f),
                        radius = greenRadius
                    ),
                    radius = greenRadius,
                    center = Offset(size.width, size.height * 0.1f)
                )

            }
    ) {

        AetherTopBar(
            title = "Revisar relatório",
            onBackClick = onBackClick
        )

        if (report == null || isLoading) {
            ReportLoadState(
                errorMessage = if (isLoading) null else errorMessage,
                onRetry = onRetry
            )
        } else {
            ReportReviewContent(
                report = report,
                onProceedClick = onProceedClick
            )
        }
    }
}

@Composable
private fun ReportReviewContent(
    report: ReportReviewUi,
    onProceedClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedScope by remember(report.scopes) { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Revisão de\nrelatório",
                    style = titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (report.reviewerName != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = report.reviewerAvatarUrl,
                            contentDescription = null,
                            placeholder = painterResource(id = R.drawable.ic_avatar_placeholder),
                            error = painterResource(id = R.drawable.ic_avatar_placeholder),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Revisado por ${report.reviewerName}",
                            style = bodyMediumMd,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clock),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Análise pendente",
                            style = bodyMediumMd,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_clipboard_illustration),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(120.dp).offset(x= 8.dp, y= -10.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = purple700.copy(alpha = 0.08f),
                    spotColor = purple700.copy(alpha = 0.08f)
                )
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Identificação",
                        style = titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (report.sector != null) {
                        Surface(
                            shape = RoundedCornerShape(100),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = report.sector,
                                style = labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text("Planta industrial", style = bodySmallMedium, color = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            report.unitName,
                            style = bodyMediumMd,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    VerticalDivider(
                        modifier = Modifier
                            .height(48.dp)
                            .padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Data de envio", style = bodySmallMedium, color = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            report.submittedAt,
                            style = bodyMediumMd,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (!report.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Descrição",
                        style = titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = report.description,
                        style = bodySmallMedium,
                        color = MaterialTheme.colorScheme.outline,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Gases identificados",
            style = titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (report.scopes.isEmpty()) {
            Text(
                text = "Nenhum gás identificado neste relatório.",
                style = bodyMediumMd,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                report.scopes.forEachIndexed { index, scope ->
                    ScopeChip(
                        text = scope.scope,
                        isSelected = index == selectedScope,
                        onClick = { selectedScope = index }
                    )
                }
            }

            report.scopes.getOrNull(selectedScope)?.gases?.forEach { gas ->
                Spacer(modifier = Modifier.height(24.dp))
                GasEmissionCard(gas = gas)
            }
        }

        if (report.reviewerName != null) {
            Spacer(modifier = Modifier.height(24.dp))

            val borderColor = if (report.isApproved) MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
            val iconRes = if (report.isApproved) R.drawable.ic_approved_circle else R.drawable.ic_rejected_circle
            val iconTint = if (report.isApproved) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
            val statusTitle = if (report.isApproved) "Relatório aprovado" else "Relatório reprovado"
            val subtitle = if (report.isApproved) "Comentário" else "Motivo"

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = statusTitle,
                            style = titleMedium,
                            color = iconTint
                        )
                    }

                    if (!report.validationMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = subtitle,
                            style = titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = report.validationMessage,
                            style = bodySmallMedium,
                            color = MaterialTheme.colorScheme.outline,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        if (report.canProceedToAnalysis) {
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(100))
                    .background(Brush.linearGradient(listOf(Color(0xFF8B5CF6), purple500)))
                    .clickable(onClick = onProceedClick),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Prosseguir para análise",
                        style = titleMediumMD,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun GasEmissionCard(gas: GasEmissionUi) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = purple700.copy(alpha = 0.08f),
                spotColor = purple700.copy(alpha = 0.08f)
            )
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 24.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = withSubscriptDigits("${gas.name} (${gas.formula})"),
                    style = titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = gas.quantity,
                    style = displayLargeBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = withSubscriptDigits("tCO2e/mês"),
                    style = bodyMediumMd,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_gas_illustration),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(120.dp)
                    .height(100.dp)
                    .offset(x = 10.dp, y = 30.dp)
            )
        }
    }
}

/** Renders chemical formula digits smaller, e.g. the 2 in "CO2". */
private fun withSubscriptDigits(text: String): AnnotatedString = buildAnnotatedString {
    text.forEachIndexed { index, char ->
        val isFormulaDigit = char.isDigit() && index > 0 && text[index - 1].isLetterOrDigit()
        if (isFormulaDigit) {
            withStyle(SpanStyle(fontSize = 10.sp)) { append(char) }
        } else {
            append(char)
        }
    }
}

@Composable
private fun ScopeChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val modifier = if (isSelected) {
        Modifier
            .background(
                brush = Brush.linearGradient(listOf(Color(0xFF8B5CF6), MaterialTheme.colorScheme.primary)),
                shape = RoundedCornerShape(100)
            )
    } else {
        Modifier
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), shape = RoundedCornerShape(100))
            .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f), RoundedCornerShape(100))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = labelMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

private val previewReport = ReportReviewUi(
    name = "Inventário 1º semestre",
    sector = "Frigorífico",
    unitName = "Frigorífico São Paulo\nUnidade 3",
    submittedAt = "21 jul 2026",
    description = "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident.",
    reviewerName = null,
    reviewerAvatarUrl = null,
    isApproved = false,
    validationMessage = null,
    scopes = listOf(
        ScopeGasesUi("Escopo 1", listOf(GasEmissionUi("Gás carbônico", "CO2", "1.240"))),
        ScopeGasesUi("Escopo 2", listOf(GasEmissionUi("Metano", "CH4", "312"))),
        ScopeGasesUi("Escopo 3", listOf(GasEmissionUi("Óxido nitroso", "N2O", "48,5")))
    ),
    canProceedToAnalysis = true
)

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 1020)
@Composable
fun ReportReviewScreenPendingPreview() {
    AetherTheme {
        ReportReviewScreen(
            report = previewReport,
            isLoading = false,
            errorMessage = null,
            onRetry = {},
            onBackClick = {},
            onProceedClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 1200)
@Composable
fun ReportReviewScreenRejectedPreview() {
    AetherTheme {
        ReportReviewScreen(
            report = previewReport.copy(
                reviewerName = "Nisflei Grandão",
                validationMessage = "Relatório com informações não coerentes.",
                canProceedToAnalysis = false
            ),
            isLoading = false,
            errorMessage = null,
            onRetry = {},
            onBackClick = {},
            onProceedClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 1200)
@Composable
fun ReportReviewScreenApprovedPreview() {
    AetherTheme {
        ReportReviewScreen(
            report = previewReport.copy(
                reviewerName = "Nisflei Grandão",
                isApproved = true,
                validationMessage = "Relatório detalhado, vai ajudar muito na nossa análise.",
                canProceedToAnalysis = false
            ),
            isLoading = false,
            errorMessage = null,
            onRetry = {},
            onBackClick = {},
            onProceedClick = {}
        )
    }
}
