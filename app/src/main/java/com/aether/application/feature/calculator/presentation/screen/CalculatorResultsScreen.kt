package com.aether.application.feature.calculator.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.aether.core.ui.components.AetherTopBar
import com.aether.core.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

data class CalculatorResultUi(
    val emissionBefore: Int,
    val emissionAfter: Int,
    val neutralized: Int,
    val percentOfTotal: Int,
    val greenGasName: String,
    val scopeLabel: String,
    val periodYears: Int,
    val axisTicks: List<Int> = listOf(500, 600, 700, 800, 900, 1000)
)

@Composable
fun CalculatorResultsScreen(
    result: CalculatorResultUi,
    onBackClick: () -> Unit,
    onFinishClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(primary.copy(alpha = 0.14f), Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = size.width
                    ),
                    radius = size.width,
                    center = Offset(0f, 0f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(secondary.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(size.width, size.height * 0.05f),
                        radius = size.width * 0.9f
                    ),
                    radius = size.width * 0.9f,
                    center = Offset(size.width, size.height * 0.05f)
                )
            }
    ) {
        AetherTopBar(title = "Resultados", onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            MainResultCard(result = result)

            Spacer(Modifier.height(32.dp))

            ImpactCard(result = result)

            Spacer(Modifier.height(32.dp))

            SimulationDataCard(result = result)

            Spacer(Modifier.height(24.dp))
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(100))
                .background(Brush.linearGradient(listOf(lerp(primary, onPrimary, 0.18f), primary)))
                .clickable(onClick = onFinishClick),
            contentAlignment = Alignment.Center
        ) {
            Text("Concluir", style = titleMediumMD, color = onPrimary)
        }
    }
}

@Composable
private fun MainResultCard(result: CalculatorResultUi) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val shape = RoundedCornerShape(24.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = primary.copy(alpha = 0.25f),
                spotColor = primary.copy(alpha = 0.25f)
            )
            .clip(shape)
            .background(Brush.linearGradient(listOf(lerp(primary, onPrimary, 0.18f), primary)))
            .drawBehind {
                drawCircle(
                    color = onPrimary.copy(alpha = 0.10f),
                    radius = size.height * 0.55f,
                    center = Offset(size.width * 0.82f, size.height * 0.98f)
                )
                drawCircle(
                    color = onPrimary.copy(alpha = 0.08f),
                    radius = size.height * 0.4f,
                    center = Offset(size.width * 0.2f, size.height * 0.02f)
                )
                val dotRadius = 1.2.dp.toPx()
                val dotGap = 9.dp.toPx()
                repeat(5) { row ->
                    repeat(6) { col ->
                        drawCircle(
                            color = onPrimary.copy(alpha = 0.35f),
                            radius = dotRadius,
                            center = Offset(
                                x = size.width - 24.dp.toPx() - col * dotGap,
                                y = 18.dp.toPx() + row * dotGap
                            )
                        )
                    }
                }
            }
            .padding(24.dp)
    ) {
        Text("Resultado Principal", style = titleLarge, color = onPrimary)
        Text("Emissões estimadas após a substituição", style = bodyLargeMedium, color = onPrimary)

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                formatNumber(result.emissionAfter),
                style = displayLargeBold,
                color = onPrimary
            )
            Spacer(Modifier.width(8.dp))
            Text(
                emissionUnit(),
                style = titleMediumMD,
                color = onPrimary,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        Text(
            "${result.percentOfTotal}% do total emitido",
            style = bodyMediumMd,
            color = onPrimary
        )

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HeroChip(
                text = result.greenGasName,
                background = onPrimary.copy(alpha = 0.78f),
                contentColor = purple900,
                borderColor = onPrimary.copy(alpha = 0.9f)
            )
            HeroChip(
                text = result.scopeLabel,
                background = lerp(secondary, onPrimary, 0.7f),
                contentColor = green700,
                borderColor = onPrimary.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun HeroChip(
    text: String,
    background: Color,
    contentColor: Color,
    borderColor: Color
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(background)
            .border(1.dp, borderColor, shape)
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(text, style = labelMedium, color = contentColor)
    }
}

@Composable
private fun ImpactCard(result: CalculatorResultUi) {
    val shape = RoundedCornerShape(24.dp)

    Surface(
        shape = shape,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            )
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
            Text(
                "Impacto da substituição",
                style = titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Quantidade neutralizada",
                style = bodyLargeMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    formatNumber(result.neutralized),
                    style = displayMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    emissionUnit(),
                    style = titleMediumMD,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            ComparisonChart(
                before = result.emissionBefore,
                after = result.emissionAfter,
                axisTicks = result.axisTicks
            )
        }
    }
}

@Composable
private fun ComparisonChart(
    before: Int,
    after: Int,
    axisTicks: List<Int>
) {
    val labelColumnWidth = 48.dp
    val divider = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(color = divider)

        ChartRow(
            label = "Antes",
            labelColumnWidth = labelColumnWidth
        ) {
            ComparisonBar(
                valueText = emissionUnit(prefix = "${formatNumber(before)} "),
                fraction = barFraction(before, axisTicks),
                barColor = MaterialTheme.colorScheme.primary,
                textColor = MaterialTheme.colorScheme.onPrimary
            )
        }

        ChartRow(
            label = "Depois",
            labelColumnWidth = labelColumnWidth
        ) {
            ComparisonBar(
                valueText = emissionUnit(prefix = "${formatNumber(after)} "),
                fraction = barFraction(after, axisTicks),
                barColor = Color(0xFF5DDBA5).copy(alpha = 0.60f),
                textColor = MaterialTheme.colorScheme.onSurface
            )
        }

        HorizontalDivider(color = divider)

        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(Modifier.width(labelColumnWidth))
            axisTicks.forEach { tick ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        tick.toString(),
                        style = bodySmallMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartRow(
    label: String,
    labelColumnWidth: androidx.compose.ui.unit.Dp,
    bar: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = bodySmallMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(labelColumnWidth)
        )
        Box(modifier = Modifier.weight(1f)) { bar() }
    }
}

@Composable
private fun ComparisonBar(
    valueText: AnnotatedString,
    fraction: Float,
    barColor: Color,
    textColor: Color
) {
    val shape = RoundedCornerShape(50)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .clip(shape)
                .background(barColor),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                valueText,
                style = bodySmallMedium,
                color = textColor,
                maxLines = 1,
                modifier = Modifier.padding(start = 12.dp, top = 2.5.dp)
            )
        }
    }
}

@Composable
private fun SimulationDataCard(result: CalculatorResultUi) {
    val primary = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(20.dp)
    val periodLabel = if (result.periodYears == 1) "ano" else "anos"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(primary.copy(alpha = 0.08f))
            .border(1.dp, primary.copy(alpha = 0.35f), shape)
            .padding(24.dp)
    ) {
        Text(
            "Dados da simulação",
            style = titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier.width(IntrinsicSize.Max),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SimulationChip("Gás utilizado: ${result.greenGasName.lowercase(Locale.forLanguageTag("pt-BR"))}")
            SimulationChip("Prazo escolhido: ${result.periodYears} $periodLabel")
        }
    }
}

@Composable
private fun SimulationChip(text: String) {
    val primary = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(50)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(primary.copy(alpha = 0.12f))
            .border(1.dp, primary.copy(alpha = 0.35f), shape)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(text, style = bodySmallMedium, color = primary)
    }
}

private fun barFraction(value: Int, ticks: List<Int>): Float {
    val min = ticks.first()
    val max = ticks.last()
    val edgePadding = 1f / (2f * ticks.size)
    val scaled = (value - min).toFloat() / (max - min)
    return (edgePadding + scaled * (1f - 2f * edgePadding)).coerceIn(0f, 1f)
}

private fun emissionUnit(prefix: String = ""): AnnotatedString = buildAnnotatedString {
    append(prefix)
    append("tCO")
    withStyle(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 0.7.em)) { append("2") }
    append("e/mês")
}

private fun formatNumber(value: Int): String =
    NumberFormat.getIntegerInstance(Locale.forLanguageTag("pt-BR")).format(value)

private val previewResult = CalculatorResultUi(
    emissionBefore = 1240,
    emissionAfter = 817,
    neutralized = 423,
    percentOfTotal = 34,
    greenGasName = "Biometano",
    scopeLabel = "Escopo 1",
    periodYears = 4
)

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CalculatorResultsScreenPreview() {
    AetherTheme {
        CalculatorResultsScreen(
            result = previewResult,
            onBackClick = {},
            onFinishClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CalculatorResultsScreenDarkPreview() {
    AetherTheme {
        CalculatorResultsScreen(
            result = previewResult,
            onBackClick = {},
            onFinishClick = {}
        )
    }
}