
package com.aether.application.feature.analysis.presentation.screen

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.R
import com.aether.core.ui.components.AetherTopBar
import com.aether.core.ui.theme.*
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class ImprovementPlanUi(
    val id: String,
    val definedProblem: String,
    val method: String,
    val reasoning: String,
    val updatedAt: String? = null
)

data class ScopeUi(
    val name: String,
    val percent: Int
)

data class ReplacementUi(
    val title: String,
    val reduction: Int,
    val percent: Int,
    val technology: String,
    val scope: String
)

data class AetherAnalysisUi(
    val totalEmissions: Int,
    val changePercent: Int,
    val isEmissionIncrease: Boolean,
    val scopes: List<ScopeUi>,
    val improvements: List<ImprovementPlanUi>,
    val priority: ReplacementUi,
    val secondary: ReplacementUi,
    val monthlySavings: Long,
    val initialInvestment: Long
)

private enum class ReportDecision {
    APPROVED,
    REJECTED
}



@Composable
fun AetherAnalysisScreen(
    analysis: AetherAnalysisUi,
    onBackClick: () -> Unit,
    onApproveClick: () -> Unit,
    onRejectClick: () -> Unit,
    modifier: Modifier = Modifier,
    onCommentSubmitted: (Boolean, String) -> Unit = { _, _ -> }
) {
    val isDarkTheme = isSystemInDarkTheme()

    val screenBackground = if (isDarkTheme) {
        MaterialTheme.colorScheme.background
    } else {
        Color.White
    }

    var decisionDialog by remember {
        mutableStateOf<ReportDecision?>(null)
    }

    var completedDecision by remember {
        mutableStateOf<ReportDecision?>(null)
    }

    var comment by remember {
        mutableStateOf("")
    }

    if (completedDecision != null) {
        ReportResultScreen(
            approved = completedDecision == ReportDecision.APPROVED,
            onBackClick = onBackClick,
            modifier = modifier
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(screenBackground)
        ) {
            AetherTopBar(
                title = "Análise do Aether",
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Visão Geral",
                    style = titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(20.dp))

                EmissionsCard(
                    total = analysis.totalEmissions,
                    changePercent = analysis.changePercent,
                    isIncrease = analysis.isEmissionIncrease
                )

                Spacer(Modifier.height(24.dp))

                ScopeCard(scopes = analysis.scopes)

                Spacer(Modifier.height(32.dp))

                ImprovementPlanCard(
                    plans = analysis.improvements
                )

                Spacer(Modifier.height(32.dp))

                ReplacementCard(
                    item = analysis.priority
                )

                Spacer(Modifier.height(32.dp))

                ReplacementCard(
                    item = analysis.secondary
                )

                Spacer(Modifier.height(32.dp))

                FinancialCard(
                    heading = "Retorno financeiro",
                    caption = "Economia mensal estimada",
                    amount = analysis.monthlySavings,
                    suffix = "/mês",
                    isGreen = true
                )

                Spacer(Modifier.height(32.dp))

                FinancialCard(
                    heading = "Investimento inicial necessário",
                    caption = null,
                    amount = analysis.initialInvestment,
                    suffix = null,
                    isGreen = false
                )

                Spacer(Modifier.height(40.dp))

                DecisionButton(
                    text = "Aprovar relatório",
                    filled = true,
                    onClick = {
                        comment = ""
                        decisionDialog = ReportDecision.APPROVED
                    }
                )

                Spacer(Modifier.height(12.dp))

                DecisionButton(
                    text = "Reprovar relatório",
                    filled = false,
                    onClick = {
                        comment = ""
                        decisionDialog = ReportDecision.REJECTED
                    }
                )

                Spacer(Modifier.height(32.dp))
            }
        }

        decisionDialog?.let { decision ->
            ReportCommentDialog(
                approved = decision == ReportDecision.APPROVED,
                comment = comment,
                onCommentChange = { comment = it },
                onDismiss = {
                    decisionDialog = null
                    comment = ""
                },
                onSubmit = {
                    val isApproved =
                        decision == ReportDecision.APPROVED

                    val submittedComment = comment.trim()

                    onCommentSubmitted(
                        isApproved,
                        submittedComment
                    )

                    if (isApproved) {
                        onApproveClick()
                    } else {
                        onRejectClick()
                    }
                    decisionDialog = null
                    completedDecision = decision
                }
            )
        }
    }
}

@Composable
private fun ReportResultScreen(
    approved: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    BackHandler(onBack = onBackClick)

    val mainColor = if (approved) {
        green500
    } else {
        Color(0xFFEF8090)
    }

    val outerColor = if (approved) {
        Color(0xFFD5F8EA)
    } else {
        Color(0xFFFACAD1)
    }

    val background = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.background
    } else {
        Color.White
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(background)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                animationSpec = tween(450)
            ) + scaleIn(
                initialScale = 0.75f,
                animationSpec = tween(550)
            ),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .clip(CircleShape)
                        .background(outerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(mainColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(
                            modifier = Modifier.size(82.dp)
                        ) {
                            val lineWidth = 11.dp.toPx()

                            if (approved) {
                                val path = Path().apply {
                                    moveTo(
                                        size.width * 0.12f,
                                        size.height * 0.53f
                                    )
                                    lineTo(
                                        size.width * 0.38f,
                                        size.height * 0.78f
                                    )
                                    lineTo(
                                        size.width * 0.91f,
                                        size.height * 0.19f
                                    )
                                }

                                drawPath(
                                    path = path,
                                    color = Color.White,
                                    style = Stroke(
                                        width = lineWidth,
                                        cap = StrokeCap.Round
                                    )
                                )
                            } else {
                                drawLine(
                                    color = Color.White,
                                    start = androidx.compose.ui.geometry.Offset(
                                        size.width * 0.20f,
                                        size.height * 0.20f
                                    ),
                                    end = androidx.compose.ui.geometry.Offset(
                                        size.width * 0.80f,
                                        size.height * 0.80f
                                    ),
                                    strokeWidth = lineWidth,
                                    cap = StrokeCap.Round
                                )

                                drawLine(
                                    color = Color.White,
                                    start = androidx.compose.ui.geometry.Offset(
                                        size.width * 0.80f,
                                        size.height * 0.20f
                                    ),
                                    end = androidx.compose.ui.geometry.Offset(
                                        size.width * 0.20f,
                                        size.height * 0.80f
                                    ),
                                    strokeWidth = lineWidth,
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(44.dp))

                Text(
                    text = if (approved) {
                        "Relatório Aprovado"
                    } else {
                        "Relatório Reprovado"
                    },
                    style = titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }

        TextButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            Text(
                text = "Voltar",
                style = titleMediumMD,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
private fun ReportCommentDialog(
    approved: Boolean,
    comment: String,
    onCommentChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val secondaryText =
        MaterialTheme.colorScheme.onSurfaceVariant

    val canSubmit = approved || comment.isNotBlank()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(
                    horizontal = 24.dp,
                    vertical = 32.dp
                )
        ) {
            Text(
                text = "Comentários",
                style = titleLarge,
                color = onSurface
            )

            Spacer(Modifier.height(28.dp))

            Text(
                text = if (approved) {
                    "Deseja adicionar um comentário?"
                } else {
                    "Descreva o motivo de rejeição"
                },
                style = bodyLargeMedium,
                color = secondaryText
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                BasicTextField(
                    value = comment,
                    onValueChange = onCommentChange,
                    modifier = Modifier.fillMaxSize(),
                    textStyle = bodyMediumMd.copy(
                        color = onSurface
                    ),
                    cursorBrush = Brush.verticalGradient(
                        listOf(primary, primary)
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopStart
                        ) {
                            if (comment.isEmpty()) {
                                Text(
                                    text = if (approved) {
                                        "Escreva um comentário (opcional)"
                                    } else {
                                        "Descreva brevemente a motivação da reprovação do relatório"
                                    },
                                    style = bodyMediumMd,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            innerTextField()
                        }
                    }
                )
            }

            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(CircleShape)
                    .background(
                        if (canSubmit) {
                            Brush.horizontalGradient(
                                listOf(
                                    primary,
                                    lerp(primary, Color.White, 0.30f)
                                )
                            )
                        } else {
                            Brush.horizontalGradient(
                                listOf(
                                    primary.copy(alpha = 0.35f),
                                    primary.copy(alpha = 0.35f)
                                )
                            )
                        }
                    )
                    .clickable(
                        enabled = canSubmit,
                        onClick = onSubmit
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Enviar",
                    style = titleMediumMD,
                    color = Color.White
                )
            }
        }
    }
}


@Composable
private fun EmissionsCard(
    total: Int,
    changePercent: Int,
    isIncrease: Boolean
) {
    val shape = RoundedCornerShape(24.dp)

    val indicatorIcon = if (isIncrease) {
        R.drawable.ic_increase
    } else {
        R.drawable.ic_decrease
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = purple500.copy(alpha = 0.20f),
                spotColor = purple500.copy(alpha = 0.20f)
            )
            .clip(shape)
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(
                id = R.drawable.bg_emissions_card
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Emissões mapeadas",
                style = titleMedium,
                color = Color.White
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Total mapeadas",
                style = bodyLargeMedium,
                color = Color.White
            )

            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = formatNumber(total),
                    style = displayMedium,
                    color = Color.White
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "tCO₂e/mês",
                    style = bodyLargeMedium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(alpha = 0.20f)
                    )
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.55f),
                        CircleShape
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 5.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${abs(changePercent.toLong())}%",
                    style = labelMedium,
                    color = Color.White
                )

                Spacer(Modifier.width(6.dp))

                Icon(
                    painter = painterResource(indicatorIcon),
                    contentDescription = if (isIncrease) {
                        "Aumento das emissões"
                    } else {
                        "Redução das emissões"
                    },
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}



@Composable
private fun AnalysisCard(
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(24.dp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = shape,
                clip = false,
                ambientColor = purple500.copy(alpha = 0.45f),
                spotColor = purple500.copy(alpha = 0.26f)
            ),
        shape = shape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.dp,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
                .copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            content = content
        )
    }
}



@Composable
private fun ScopeCard(
    scopes: List<ScopeUi>
) {
    AnalysisCard {
        Text(
            text = "Divisão por escopo",
            style = titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(24.dp))

        scopes.forEachIndexed { index, scope ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = scope.name,
                    style = bodyMediumMd,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${scope.percent}%",
                    style = bodyMediumMd,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.outlineVariant
                            .copy(alpha = 0.30f)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(
                            (scope.percent / 100f)
                                .coerceIn(0f, 1f)
                        )
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.secondary
                        )
                )
            }

            if (index != scopes.lastIndex) {
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun ImprovementPlanCard(
    plans: List<ImprovementPlanUi>
) {
    AnalysisCard {
        Text(
            text = "Plano de melhorias",
            style = titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(24.dp))

        if (plans.isEmpty()) {
            Text(
                text = "Nenhum plano de melhoria disponível.",
                style = bodyMediumMd,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            plans.forEachIndexed { index, plan ->
                var expanded by remember(plan.id) {
                    mutableStateOf(false)
                }

                PlanDetailRow(
                    icon = R.drawable.ic_problem,
                    title = "Problema identificado",
                    description = plan.definedProblem,
                    iconBackground = MaterialTheme.colorScheme.error
                        .copy(alpha = 0.12f),
                    iconTint = MaterialTheme.colorScheme.error
                )

                PlanDivider()

                PlanDetailRow(
                    icon = R.drawable.ic_method,
                    title = "Método recomendado",
                    description = plan.method,
                    iconBackground = MaterialTheme.colorScheme.secondary
                        .copy(alpha = 0.12f),
                    iconTint = Color.Unspecified
                )

                PlanDivider()

                PlanDetailRow(
                    icon = R.drawable.ic_justification,
                    title = "Justificativa técnica",
                    description = plan.reasoning,
                    iconBackground = MaterialTheme.colorScheme.primary
                        .copy(alpha = 0.12f),
                    iconTint = MaterialTheme.colorScheme.primary,
                    expanded = expanded,
                    onExpand = { expanded = !expanded }
                )

                if (index != plans.lastIndex) {
                    Spacer(Modifier.height(20.dp))

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant
                            .copy(alpha = 0.5f)
                    )

                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun PlanDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant
            .copy(alpha = 0.4f)
    )
}

@Composable
private fun PlanDetailRow(
    @DrawableRes icon: Int,
    title: String,
    description: String,
    iconBackground: Color,
    iconTint: Color,
    expanded: Boolean = true,
    onExpand: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = description,
                style = bodySmallMedium,
                color = MaterialTheme.colorScheme.outline,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (onExpand != null) {
            Spacer(Modifier.width(8.dp))

            Text(
                text = if (expanded) "Ver menos" else "Ver tudo",
                style = labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable(onClick = onExpand)
            )
        }
    }
}

@Composable
private fun ReplacementCard(
    item: ReplacementUi
) {
    AnalysisCard {
        Text(
            text = item.title,
            style = titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Redução em número",
            style = bodyLargeMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatNumber(item.reduction),
                style = displayMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.width(6.dp))

            Text(
                text = "tCO₂e/mês",
                style = bodyMediumMd,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.width(12.dp))

            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.error
                            .copy(alpha = 0.12f)
                    )
                    .padding(
                        horizontal = 10.dp,
                        vertical = 4.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${abs(item.percent.toLong())}%",
                    style = labelMedium,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(Modifier.width(4.dp))

                Icon(
                    painter = painterResource(
                        R.drawable.ic_decrease
                    ),
                    contentDescription = "Redução",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        AnalysisChip(
            text = item.technology,
            textColor = MaterialTheme.colorScheme.primary,
            fill = MaterialTheme.colorScheme.primary
                .copy(alpha = 0.12f)
        )

        Spacer(Modifier.height(8.dp))

        AnalysisChip(
            text = item.scope,
            textColor = MaterialTheme.colorScheme.secondary,
            fill = MaterialTheme.colorScheme.secondary
                .copy(alpha = 0.12f)
        )
    }
}

@Composable
private fun AnalysisChip(
    text: String,
    textColor: Color,
    fill: Color
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(fill)
            .border(
                1.dp,
                textColor.copy(alpha = 0.35f),
                CircleShape
            )
            .padding(
                horizontal = 14.dp,
                vertical = 6.dp
            )
    ) {
        Text(
            text = text,
            style = labelMedium,
            color = textColor
        )
    }
}

@Composable
private fun FinancialCard(
    heading: String,
    caption: String?,
    amount: Long,
    suffix: String?,
    isGreen: Boolean
) {
    val shape = RoundedCornerShape(20.dp)

    val accent = if (isGreen) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = shape,
                clip = false,
                ambientColor = accent.copy(alpha = 0.035f),
                spotColor = accent.copy(alpha = 0.05f)
            )
            .clip(shape)
            .background(accent.copy(alpha = 0.12f))
            .border(
                1.dp,
                accent.copy(alpha = 0.4f),
                shape
            )
            .padding(24.dp)
    ) {
        Text(
            text = heading,
            style = titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (caption != null) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = caption,
                style = bodyLargeMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "R$ ${formatNumber(amount)}",
                style = displayMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (suffix != null) {
                Spacer(Modifier.width(6.dp))

                Text(
                    text = suffix,
                    style = bodyMediumMd,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}


@Composable
private fun DecisionButton(
    text: String,
    filled: Boolean,
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val error = MaterialTheme.colorScheme.error
    val shape = CircleShape

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .then(
                if (filled) {
                    Modifier.background(
                        Brush.horizontalGradient(
                            listOf(
                                primary,
                                lerp(primary, Color.White, 0.30f)
                            )
                        )
                    )
                } else {
                    Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, error, shape)
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = titleMediumMD,
            color = if (filled) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                error
            }
        )
    }
}

private fun formatNumber(value: Number): String =
    NumberFormat.getIntegerInstance(
        Locale.forLanguageTag("pt-BR")
    ).format(value)

private val previewAnalysis = AetherAnalysisUi(
    totalEmissions = 2403,
    changePercent = 12,
    isEmissionIncrease = true,
    scopes = listOf(
        ScopeUi("Escopo 1", 63),
        ScopeUi("Escopo 2", 27),
        ScopeUi("Escopo 3", 10)
    ),
    improvements = listOf(
        ImprovementPlanUi(
            id = "1",
            definedProblem = "Elevada emissão no processo produtivo",
            method = "Substituir combustível fóssil por biometano.",
            reasoning = "A utilização de biometano pode reduzir as emissões associadas ao consumo de combustíveis fósseis, dependendo das características do processo produtivo e da origem do combustível."
        )
    ),
    priority = ReplacementUi(
        title = "Substituição prioritária",
        reduction = 817,
        percent = 34,
        technology = "Biometano",
        scope = "Escopo 1"
    ),
    secondary = ReplacementUi(
        title = "Substituição secundária",
        reduction = 264,
        percent = 11,
        technology = "Energia solar fotovoltaica",
        scope = "Escopo 2"
    ),
    monthlySavings = 87400,
    initialInvestment = 1220000
)

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    heightDp = 2000
)
@Composable
private fun AetherAnalysisLightPreview() {
    AetherTheme(darkTheme = false) {
        AetherAnalysisScreen(
            analysis = previewAnalysis,
            onBackClick = {},
            onApproveClick = {},
            onRejectClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    heightDp = 2000
)
@Composable
private fun AetherAnalysisDarkPreview() {
    AetherTheme(darkTheme = true) {
        AetherAnalysisScreen(
            analysis = previewAnalysis,
            onBackClick = {},
            onApproveClick = {},
            onRejectClick = {}
        )
    }
}
