package com.aether.application.feature.settings.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.core.ui.components.AetherTopBar
import com.aether.core.ui.components.FormDropdownField
import com.aether.core.ui.theme.*
import com.aether.application.R

@Composable
fun ReportProblemScreen(
    category: String,
    description: String,
    attachedFileName: String?,
    onCategoryChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSelectFileClicked: () -> Unit,
    onRemoveFileClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onSubmitClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AetherTopBar(title = "Relatar problema", onBackClick = onBackClicked)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            FormDropdownField(
                label = "Categoria do problema*",
                placeholder = "Selecione a categoria",
                value = category,
                options = listOf("Erro no app", "Dados incorretos", "Lentidão", "Outro"),
                onValueSelected = onCategoryChanged
            )

            Spacer(Modifier.height(24.dp))

            RequiredFieldLabel(text = "Descrição do problema")
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChanged,
                    placeholder = { Text("Descreva detalhadamente o que aconteceu") },
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purple300,
                        unfocusedBorderColor = textDisabledLight,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )
                )
                Text(
                    text = "${description.length}/500",
                    style = bodySmall,
                    color = textTertiaryLight,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 16.dp, end = 16.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Row {
                Text("Anexar print ", style = bodyMediumMd, color = textPrimaryLight)
                Text("(Opcional)", style = bodyMediumMd, color = textTertiaryLight)
            }
            Spacer(Modifier.height(8.dp))

            FileDropzone(
                fileName = attachedFileName,
                onSelectFileClicked = onSelectFileClicked,
                onRemoveFileClicked = onRemoveFileClicked
            )

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Brush.linearGradient(listOf(Color(0xFF7C5CFC), purple500)))
                    .clickable(onClick = onSubmitClicked),
                contentAlignment = Alignment.Center
            ) {
                Text("Enviar", style = titleMediumMD, color = Color.White)
            }
        }
    }
}

@Composable
private fun RequiredFieldLabel(text: String) {
    Text(
        text = buildAnnotatedString {
            append(text)
            withStyle(style = SpanStyle(color = Color(0xFFD32F2F))) {
                append("*")
            }
        },
        style = labelMedium,
        color = textPrimaryLight
    )
}

@Composable
private fun FileDropzone(
    fileName: String?,
    onSelectFileClicked: () -> Unit,
    onRemoveFileClicked: () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(shape)
            .background(green500.copy(alpha = 0.06f))
            .dashedBorder(color = green500, cornerRadius = 20.dp)
            .clickable(onClick = onSelectFileClicked)
            .padding(16.dp)
    ) {
        if (fileName == null) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_file),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(Modifier.height(10.dp))
                Text("Selecione um arquivo", style = bodyMedium, fontWeight = FontWeight.Bold, color = green700)
                Spacer(Modifier.height(2.dp))
                Text("PNG, JPG, JPEG", style = bodySmallMedium, color = green500)
            }
        } else {
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_file),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(fileName, style = bodyMedium, fontWeight = FontWeight.Bold, color = textPrimaryLight)
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = onRemoveFileClicked, modifier = Modifier.size(22.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remover arquivo",
                        tint = textTertiaryLight
                    )
                }
            }
        }
    }
}

private fun Modifier.dashedBorder(
    color: Color,
    cornerRadius: androidx.compose.ui.unit.Dp,
    strokeWidth: androidx.compose.ui.unit.Dp = 1.5.dp,
    dashLength: androidx.compose.ui.unit.Dp = 6.dp,
    gapLength: androidx.compose.ui.unit.Dp = 5.dp
): Modifier = this.drawWithContent {
    drawContent()
    drawRoundRect(
        color = color,
        style = Stroke(
            width = strokeWidth.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength.toPx(), gapLength.toPx()), 0f)
        ),
        cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ReportProblemScreenPreview() {
    AetherTheme {
        ReportProblemScreen(
            category = "",
            description = "",
            attachedFileName = null,
            onCategoryChanged = {},
            onDescriptionChanged = {},
            onSelectFileClicked = {},
            onRemoveFileClicked = {},
            onBackClicked = {},
            onSubmitClicked = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ReportProblemScreenWithFilePreview() {
    AetherTheme {
        ReportProblemScreen(
            category = "Erro no app",
            description = "O botão de enviar não responde ao toque.",
            attachedFileName = "print_erro.png",
            onCategoryChanged = {},
            onDescriptionChanged = {},
            onSelectFileClicked = {},
            onRemoveFileClicked = {},
            onBackClicked = {},
            onSubmitClicked = {}
        )
    }
}