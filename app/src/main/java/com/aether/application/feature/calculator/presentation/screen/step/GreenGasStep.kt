package com.aether.application.feature.calculator.presentation.screen.step

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.core.ui.components.AetherTopBar
import com.aether.core.ui.components.FormDropdownField
import com.aether.core.ui.theme.*

@Composable
fun GreenGasStep(
    greenGas: String,
    onGreenGasChanged: (String) -> Unit,
    onConsultAekoClicked: () -> Unit = {},
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
            Text("Gás Verde", style = titleLarge, color = textPrimaryLight)
            Spacer(Modifier.height(32.dp))

            Text(
                text = buildAnnotatedString {
                    append("Gás verde que deseja simular")
                    withStyle(style = SpanStyle(color = lightRed)) {
                        append("*")
                    }
                },
                style = labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = textPrimaryLight
            )
            Spacer(Modifier.height(8.dp))

            FormDropdownField(
                label = "",
                placeholder = "Selecione o gás verde",
                value = greenGas,
                options = listOf("Biometano", "Hidrogênio Verde", "Etanol"),
                onValueSelected = onGreenGasChanged
            )

            Spacer(Modifier.height(460.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = textTertiaryLight)) {
                        append("Não sabe qual escolher? ")
                    }
                    withStyle(style = SpanStyle(color = purple500, fontWeight = FontWeight.SemiBold)) {
                        append("Consulte o Aeko")
                    }
                },
                style = bodyMediumMd,
                modifier = Modifier
                    .padding(start = 36.dp)
                    .clickable { onConsultAekoClicked() }
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
                            .size(6.dp)
                            .background(textDisabledLight, RoundedCornerShape(50))
                    )
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(6.dp)
                            .background(green500, RoundedCornerShape(50))
                    )
                }

                IconButton(
                    onClick = onNextClicked,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(56.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(purple500, purple300)
                            ),
                            shape = RoundedCornerShape(50)
                        )
                        .shadow(
                            elevation = 10.dp,
                            shape = CircleShape,
                            ambientColor = purple500,
                            spotColor = purple500.copy(alpha = 0.65f)
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Prosseguir",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun GreenGasStepPreview() {
    AetherTheme {
        GreenGasStep(
            greenGas = "",
            onGreenGasChanged = {}
        )
    }
}