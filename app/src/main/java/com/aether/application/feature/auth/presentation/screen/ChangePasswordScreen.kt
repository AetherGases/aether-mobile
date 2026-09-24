package com.aether.application.feature.auth.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.R
import com.aether.application.feature.auth.domain.model.DefaultPasswordRules
import com.aether.application.feature.auth.domain.model.PasswordRule
import com.aether.core.ui.theme.*
import com.aether.application.ui.components.RequirementItem
import com.aether.core.ui.components.GlassIconButton

@Composable
fun ChangePasswordScreen(
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    isConfirmStep: Boolean,
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    rules: List<PasswordRule> = DefaultPasswordRules
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
                        colors = listOf(purple300, purple300.copy(alpha = 0.1f)),
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
                        colors = listOf(green300.copy(alpha = 0.1f), green300),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(64.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                GlassIconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_left),
                        contentDescription = "Voltar",
                        tint = textPrimaryLight
                    )
                }
                Text(
                text = "Recuperação de senha",
                style = titleMedium,
                color = textPrimaryLight,
                modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(Modifier.height(64.dp))

            Text(
                text = "Insira a nova \nsenha",
                style = displayMedium,
                color = textPrimaryLight
            )

            Spacer(Modifier.height(24.dp))

            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textPrimaryLight,
                unfocusedTextColor = textPrimaryLight,
                focusedBorderColor = purple300,
                unfocusedBorderColor = textDisabledLight,
                disabledBorderColor = textDisabledLight,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorTextColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error,
                errorCursorColor = MaterialTheme.colorScheme.error
            )

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = {
                    Text(
                        text = "Nova Senha:",
                        style = bodyLarge,
                        color = textPrimaryLight
                    )
                },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isConfirmStep,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(50.dp),
                isError = errorMessage != null && !isConfirmStep
            )

            AnimatedVisibility(visible = isConfirmStep) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    placeholder = {
                        Text(
                            text = "Confirmar Senha:",
                            style = bodyLarge,
                            color = textPrimaryLight
                        )
                    },
                    colors = textFieldColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(50.dp),
                    isError = errorMessage != null
                )
            }

            if (errorMessage != null) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    style = labelMedium,
                    color = lightRed
                )
            }

            Spacer(Modifier.height(24.dp))

            Column(modifier) {
                Text(
                    text = "A sua senha deve conter",
                    color = textPrimaryLight,
                    style = titleMedium,
                )
                Spacer(Modifier.height(20.dp))
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    rules.forEach { rule ->
                        RequirementItem(
                            text = rule.label,
                            isMet = rule.isSatisfiedBy(password),
                        )
                    }
                }
            }


            Spacer(Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ){
                Button(
                    onClick = onSubmitClick,
                    enabled = !isLoading,
                    modifier = Modifier.height(50.dp).shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = Color(0xFF000000),
                        spotColor = green500
                    ),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = green500)

                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = textPrimaryDark,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (isConfirmStep) "Trocar" else "Confirmar",
                            style = titleMedium,
                            color = textPrimaryDark
                        )
                    }
                }
            }



            Spacer(Modifier.height(24.dp))
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ChangePasswordScreenPreview() {
    AetherTheme {
        ChangePasswordScreen(
            password = "Algo1",
            onPasswordChange = {},
            confirmPassword = "",
            onConfirmPasswordChange = {},
            isConfirmStep = false,
            onBackClick = {},
            onSubmitClick = {}
        )
    }
}
