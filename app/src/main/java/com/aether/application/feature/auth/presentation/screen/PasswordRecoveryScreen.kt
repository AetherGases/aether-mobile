package com.aether.application.feature.auth.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.core.ui.components.GlassIconButton
import com.aether.core.ui.theme.*

@Composable
fun PasswordRecoveryScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    onSendCodeClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(green100, purple100, backgroundLightElevated),
                    start = Offset(0f, 0f),
                    end = Offset(800f, 1200f)
                )
            )
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = (-60).dp, y = (-60).dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(green300, green300.copy(alpha = 0.1f)),
                        start = Offset(0f, 0f),
                        end = Offset(0f, 500f)
                    )
                )
                .blur(40.dp)
        )

        Box(
            modifier = Modifier
                .size(290.dp)
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = 60.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(purple300.copy(alpha = 0.1f), purple300),
                        start = Offset(0f, 400f),
                        end = Offset(0f, 0f)
                    )
                )
                .blur(50.dp)
        )

        Box(
            modifier = Modifier
                .size(110.dp)
                .align(Alignment.TopStart)
                .offset(x = 120.dp, y = 200.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(green100.copy(alpha = 0.1f), green100),
                        start = Offset(0f, 0f),
                        end = Offset(0f, 600f)
                    ))
                .blur(30.dp)
        )
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopStart)
                .offset(x = 30.dp, y = 140.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(purple300.copy(alpha = 0.1f), purple300),
                        start = Offset(0f, 0f),
                        end = Offset(0f, 750f)
                    ))
                .blur(30.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(64.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Recuperação de senha",
                    style = titleMedium,
                    color = textPrimaryLight,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(Modifier.height(64.dp))

            Text(
                text = "Nos informe seu email",
                style = displayMedium,
                color = textPrimaryLight
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                placeholder = {
                    Text(
                        text = "Email:",
                        style = bodyLarge,
                        color = textPrimaryLight
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textPrimaryLight,
                    unfocusedTextColor = textPrimaryLight,
                    focusedBorderColor = purple300,
                    unfocusedBorderColor = textDisabledLight,
                    disabledBorderColor = textDisabledLight,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorTextColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error,
                    errorCursorColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(50.dp),
                isError = errorMessage != null
            )

            if (errorMessage != null) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    style = labelMedium,
                    color = lightRed
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onSendCodeClick,
                modifier = Modifier.fillMaxWidth().height(50.dp).shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color(0xFF000000),
                    spotColor = green500
                ),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = green500)

            ) {
                Text(text = "Enviar código de recuperação", style = titleMedium, color = textPrimaryDark)
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onBackToLoginClick,
                modifier = Modifier.fillMaxWidth().height(50.dp).border(
                    width = 1.dp,
                    color = textDisabledLight.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(28.dp)
                ).shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color(0xFF000000),
                    spotColor = backgroundLight
                ),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = backgroundLight)
            ) {
                Text(text = "Voltar ao login", style = titleMedium, color = textPrimaryLight)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PasswordRecoveryScreenPreview() {
    AetherTheme {
        PasswordRecoveryScreen(
            email = "",
            onEmailChange = {},
            onSendCodeClick = {},
            onBackToLoginClick = {}
        )
    }
}
