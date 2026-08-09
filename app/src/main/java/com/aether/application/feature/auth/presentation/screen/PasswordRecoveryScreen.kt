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
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.core.ui.components.GlassIconButton
import com.aether.core.ui.theme.*
@Composable
fun PasswordRecoveryScreen(
    email: String,
    onBackClick: () -> Unit,
    onResendClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier
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
                GlassIconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        painter = painterResource(id = com.aether.application.R.drawable.ic_chevron_left),
                        contentDescription = "Voltar",
                        tint = textPrimaryLight
                    )                }
                Text(
                    text = "Recuperação de senha",
                    style = titleMedium,
                    color = textPrimaryLight,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(Modifier.height(64.dp))

            Text(
                text = "Verifique o email cadastrado",
                style = displayMedium,
                color = textPrimaryLight
            )

            Spacer(Modifier.height(35.dp))

            val message = buildAnnotatedString {
                append("Enviamos um email para ")
                withStyle(style = androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(email)
                }
                append(". Clique no link enviado para redefinir sua senha.")
            }
            Text(
                text = message,
                style = bodyLargeMedium,
                color = textPrimaryLight
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Não recebeu um email? Verifique a caixa de spam ou solicite um novo envio.",
                style = bodySmallMedium,
                color = textSecondaryLight
            )

            Spacer(Modifier.height(284.dp))

            Button(
                onClick = onResendClick,
                modifier = Modifier.fillMaxWidth().height(50.dp).shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color(0xFF000000),
                    spotColor = green500
                ),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = green500)

            ) {
                Text(text = "Reenviar email de recuperação", style = titleMedium, color = textPrimaryDark)
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
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PasswordRecoveryScreenPreview() {
    AetherTheme {
        PasswordRecoveryScreen(
            email = "daniel.sagaz@empresajbs.com", // aq nao é hardcode nao,o email entra como parametro. Aq é só pra visualizar
            onBackClick = {},
            onResendClick = {},
            onBackToLoginClick = {}
        )
    }
}
