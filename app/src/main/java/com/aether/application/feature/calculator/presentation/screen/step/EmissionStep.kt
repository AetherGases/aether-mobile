package com.aether.application.feature.calculator.presentation.screen.step

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.core.ui.components.AetherTopBar
import com.aether.core.ui.components.FormDropdownField
import com.aether.core.ui.theme.*

@Composable
fun EmissionStep(
    pollutingGasType: String,
    monthlyVolume: String,
    scope: String,
    gasOptions: List<String>,
    onGasTypeChanged: (String) -> Unit,
    onVolumeChanged: (String) -> Unit,
    onScopeChanged: (String) -> Unit,
    onBackClicked: () -> Unit = {},
    onNextClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        AetherTopBar(
            title = "Inserir dados",
            onBackClick = onBackClicked
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Text("Emissão Atual", style = titleLarge, color = MaterialTheme.colorScheme.onBackground)

            Spacer(Modifier.height(32.dp))

            Text(
                text = buildAnnotatedString {
                    append("Tipo de gás poluente")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                        append("*")
                    }
                },
                style = labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))

            FormDropdownField(
                label = "",
                placeholder = "Selecione o gás atual",
                value = pollutingGasType,
                options = gasOptions,
                onValueSelected = onGasTypeChanged
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = buildAnnotatedString {
                    append("Volume emitido por mês")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                        append("*")
                    }
                },
                style = labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = monthlyVolume,
                onValueChange = onVolumeChanged,
                placeholder = { Text("Ex: 1.700") },
                trailingIcon = {
                    Text(
                        text = "tCO₂e/mês",
                        style = bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(percent = 50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = buildAnnotatedString {
                    append("Escopo")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                        append("*")
                    }
                },
                style = labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))

            FormDropdownField(
                label = "",
                placeholder = "Escopo de emissão",
                value = scope,
                options = listOf("Escopo 1", "Escopo 2", "Escopo 3"),
                onValueSelected = onScopeChanged
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-12).dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(6.dp)
                            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(50))
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(50))
                    )
                }

                IconButton(
                    onClick = onNextClicked,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(56.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.inversePrimary
                                )
                            ),
                            shape = RoundedCornerShape(50)
                        )
                        .shadow(
                            elevation = 10.dp,
                            shape = CircleShape,
                            ambientColor = MaterialTheme.colorScheme.primary,
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Prosseguir",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun EmissionStepPreview() {
    AetherTheme {
        EmissionStep(
            pollutingGasType = "",
            monthlyVolume = "",
            scope = "",
            gasOptions = listOf("CO₂", "CH₄"),
            onGasTypeChanged = {},
            onVolumeChanged = {},
            onScopeChanged = {}
        )
    }
}