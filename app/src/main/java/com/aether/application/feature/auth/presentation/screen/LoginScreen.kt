package com.aether.application.feature.auth.presentation.screen

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.core.ui.theme.*

@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String, rememberMe: Boolean) -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

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
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Login",
                style = displayMedium,
                color = textPrimaryLight
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text(
                        text = "Email:",
                        style = bodyLarge,
                        color = textPrimaryLight
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = purple300,
                    unfocusedBorderColor = textDisabledLight,
                    disabledBorderColor = textDisabledLight,
                    errorBorderColor = lightRed
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(50.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text(
                        text = "Senha:",
                        style = bodyLarge,
                        color = textPrimaryLight
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = purple300,
                    unfocusedBorderColor = textDisabledLight,
                    disabledBorderColor = textDisabledLight,
                    errorBorderColor = lightRed
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(50.dp)
            )

            Row(
                modifier = Modifier.padding(start = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (rememberMe) purple500 else Color.Transparent)
                            .border(2.dp, purple500, RoundedCornerShape(6.dp))
                            .clickable { rememberMe = !rememberMe }
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Lembrar-se de mim",
                        style = labelMedium,
                        color = textTertiaryLight
                    )
                }
                Spacer(Modifier.width(30.dp))
                TextButton(onForgotPasswordClick) {
                    Text(
                        text = "Esqueceu sua senha?",
                        style = labelMedium,
                        color = purple500
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { onLoginClick(email, password, rememberMe) },
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = green500),
                    modifier = Modifier
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(28.dp),
                            ambientColor = Color(0xFF000000),
                            spotColor = green500
                        )
                        .height(54.dp)
                ) {
                    Text(text = "Começar", style = titleMedium, color = textPrimaryDark)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AetherTheme {
        LoginScreen(
            onLoginClick = { _, _, _ -> },
            onForgotPasswordClick = {}
        )
    }
}