package com.aether.application.feature.calculator.presentation.screen.step

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.aether.core.ui.components.AetherTopBar
import com.aether.core.ui.theme.*

@Composable
fun DeadlineStep(
    deadline: String,
    onDeadlineChanged: (String) -> Unit,
    onBackClicked: () -> Unit = {},
    onCalculateClicked: () -> Unit = {},
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

            Text("Prazo", style = titleLarge, color = textPrimaryLight)

            Spacer(Modifier.height(32.dp))

            Text(
                text = buildAnnotatedString {
                    append("Prazo que deseja para neutralizar")
                    withStyle(style = SpanStyle(color = lightRed)) {
                        append("*")
                    }
                },
                style = labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = textPrimaryLight
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = deadline,
                onValueChange = onDeadlineChanged,
                placeholder = { Text("Ex: 5") },
                trailingIcon = {
                    Text(
                        text = "anos",
                        style = bodyLarge,
                        color = textSecondaryLight,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(percent = 50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = purple300,
                    unfocusedBorderColor = textDisabledLight,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            ) {
                Button(
                    onClick = onCalculateClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(50),
                            ambientColor = purple500,
                            spotColor = purple500.copy(alpha = 0.65f)
                        )
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(purple500, purple300)
                            ),
                            shape = RoundedCornerShape(50)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues()
                ) {
                    Text(
                        text = "Calcular",
                        color = Color.White,
                        style = titleSmall,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun DeadlineStepPreview() {
    AetherTheme {
        DeadlineStep(
            deadline = "",
            onDeadlineChanged = {}
        )
    }
}