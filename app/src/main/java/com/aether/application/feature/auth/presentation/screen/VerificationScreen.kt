package com.aether.application.feature.auth.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import com.aether.application.R
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.core.ui.components.GlassIconButton
import com.aether.core.ui.theme.*

@Composable
fun VerificationScreen(
    code: List<String>,
    onCodeChange: (List<String>) -> Unit,
    onBackClick: () -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit,
    modifier: Modifier = Modifier
){
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
                        colors = listOf(purple300.copy(alpha = 0.1f), purple300.copy(alpha = 0.7f)),
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
                        painter = painterResource(id = R.drawable.ic_chevron_left),
                        contentDescription = "Voltar",
                        tint = textPrimaryLight
                    )                }
                Text(
                    text = "Verificação",
                    style = titleMedium,
                    color = textPrimaryLight,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(Modifier.height(64.dp))

            Text(
                text = "Coloque o código recebido",
                style = displayMedium,
                color = textPrimaryLight
            )

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                code.forEachIndexed { index, digit ->
                    OutlinedTextField(
                        value = digit,
                        onValueChange = { newValue ->
                            if (newValue.length <= 1){
                                onCodeChange(code.toMutableList().also { it[index] = newValue })
                            }
                        },
                        modifier = Modifier.width(48.dp).height(54.dp),
                        singleLine = true,
                        textStyle = bodyLarge.copy(textAlign = TextAlign.Center),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = purple500,
                            unfocusedTextColor = purple300
                        )
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            Text(
                text = "Digite o código de 4 digitos enviado\npara o seu e-mail."
            )
            Spacer(Modifier.height(16.dp))

            Row{
                Text(
                    text = "Não recebeu o código?",
                    style = bodyLargeMedium,
                    color = textPrimaryLight
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Reenviar",
                    style = bodyLargeMedium,
                    color = purple500,
                    modifier = Modifier.clickable {onResendClick()}
                )
            }

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onVerifyClick,
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
                    Text(text = "Verificar", style = titleMedium, color = textPrimaryDark)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerificationScreenPreview(){
    AetherTheme() {
        VerificationScreen(
            code = List(6) { "" },
            onCodeChange = {},
            onBackClick = {},
            onVerifyClick = {},
            onResendClick = {}
        )
    }
}
