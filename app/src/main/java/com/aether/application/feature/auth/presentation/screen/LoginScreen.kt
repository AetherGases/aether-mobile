package com.aether.application.feature.auth.presentation.screen

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import com.aether.application.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.core.ui.theme.*
import com.aether.application.core.ui.components.FormErrorText
import kotlinx.coroutines.delay

private const val PASSWORD_CHAR_REVEAL_MILLIS = 500L

@Composable
fun LoginScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    rememberMe: Boolean,
    onRememberMeChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onChangeServerClick: (() -> Unit)? = null
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var revealedCharIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(revealedCharIndex) {
        if (revealedCharIndex >= 0) {
            delay(timeMillis = PASSWORD_CHAR_REVEAL_MILLIS)
            revealedCharIndex = -1
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val brush = Brush.linearGradient(
                    colors = listOf(green100, purple100, backgroundLightElevated),
                    start = Offset.Zero,
                    end = Offset(size.width, size.height / 2f)
                )

                onDrawBehind {
                    drawRect(brush)
                }
            }
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
                .imePadding()
                .verticalScroll(rememberScrollState())
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

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { newPassword ->
                    revealedCharIndex = if (newPassword.length == password.length + 1) {
                        newPassword.indices.firstOrNull { it >= password.length || newPassword[it] != password[it] } ?: -1
                    } else {
                        -1
                    }
                    onPasswordChange(newPassword)
                },
                placeholder = {
                    Text(
                        text = "Senha:",
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
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    RevealLastCharPasswordTransformation(revealedCharIndex)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha",
                            tint = textTertiaryLight
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(50.dp),
                isError = errorMessage != null
            )

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 5.dp),
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
                            .clickable { onRememberMeChange(!rememberMe) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (rememberMe) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_checked),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Lembrar-se de mim",
                        style = labelMedium,
                        color = textTertiaryLight
                    )
                }
                TextButton(onClick = onForgotPasswordClick) {
                    Text(
                        text = "Esqueceu sua senha?",
                        style = labelMedium,
                        color = purple500
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(Modifier.height(16.dp))
                FormErrorText(errorMessage)
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onLoginClick,
                    enabled = !isLoading,
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
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = textPrimaryDark,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Começar", style = titleMedium, color = textPrimaryDark)
                    }
                }
            }

            if (onChangeServerClick != null) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onChangeServerClick) {
                        Text(
                            text = "Trocar servidor (QA)",
                            style = labelMedium,
                            color = textTertiaryLight
                        )
                    }
                }
            }
        }
    }
}

/** Masks the password like [androidx.compose.ui.text.input.PasswordVisualTransformation], except the char at [revealedIndex]. */
private data class RevealLastCharPasswordTransformation(
    private val revealedIndex: Int,
    private val mask: Char = '•'
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val masked = text.text.mapIndexed { i, c -> if (i == revealedIndex) c else mask }.joinToString("")
        return TransformedText(AnnotatedString(masked), OffsetMapping.Identity)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AetherTheme {
        LoginScreen(
            email = "",
            onEmailChange = {},
            password = "",
            onPasswordChange = {},
            rememberMe = false,
            onRememberMeChange = {},
            onLoginClick = {},
            onForgotPasswordClick = {},
            isLoading = false,
            errorMessage = null
        )
    }
}
