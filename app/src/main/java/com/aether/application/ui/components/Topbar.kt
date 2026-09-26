package com.aether.core.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aether.application.R
import com.aether.core.ui.theme.AetherTheme
import com.aether.core.ui.theme.green500

@Composable
fun AetherTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val isDark = isSystemInDarkTheme()
    val titleColor = if (isDark) Color.White else Color(0xFF1E0A4A)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ThemeAwareIconButton(
            painter = painterResource(id = R.drawable.ic_chevron_left),
            contentDescription = "Voltar",
            onClick = onBackClick,
            iconSize = 16.dp
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = titleColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(end = if (trailingContent == null) 44.dp else 0.dp)
        )

        if (trailingContent != null) {
            trailingContent()
        }
    }
}

@Composable
fun AetherTrailingIconButton(
    painter: Painter,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color? = null
) {
    ThemeAwareIconButton(
        painter = painter,
        contentDescription = contentDescription,
        onClick = onClick,
        iconSize = 24.dp,
        modifier = modifier,
        tint = tint
    )
}

@Composable
private fun ThemeAwareIconButton(
    painter: Painter,
    contentDescription: String?,
    onClick: () -> Unit,
    iconSize: Dp,
    modifier: Modifier = Modifier,
    tint: Color? = null
) {
    val isDark = isSystemInDarkTheme()
    val finalTint = tint ?: if (isDark) Color.White else Color(0xFF1E0A4A)

    var buttonModifier = modifier.size(44.dp)

    buttonModifier = if (isDark) {
        buttonModifier
            .shadow(
                elevation = 10.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.32f),
                spotColor = Color.Black.copy(alpha = 0.28f)
            )
            .background(
                brush = Brush.linearGradient(
                    listOf(Color(0xFF111728), Color(0xFF1B2232), Color(0xFF252D3E))
                ),
                shape = CircleShape
            )
            .border(
                border = BorderStroke(
                    0.8.dp,
                    Brush.linearGradient(
                        listOf(Color(0xFFE3E5ED), Color(0xFF7F899C), Color(0xFF5D6879))
                    )
                ),
                shape = CircleShape
            )
    } else {
        buttonModifier
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = Color(0x25000000),
                spotColor = Color(0x25000000)
            )
            .background(
                color = Color.White.copy(alpha = 0.72f),
                shape = CircleShape
            )
    }

    IconButton(
        onClick = onClick,
        modifier = buttonModifier
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = finalTint,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F4F8)
@Composable
fun AetherTopBarLightPreview() {
    AetherTheme(darkTheme = false) {
        AetherTopBar(title = "Apenas voltar", onBackClick = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F4F8)
@Composable
fun AetherTopBarWithTrailingLightPreview() {
    AetherTheme(darkTheme = false) {
        AetherTopBar(
            title = "Conteúdo fulano",
            onBackClick = {},
            trailingContent = {
                AetherTrailingIconButton(
                    painter = painterResource(id = R.drawable.ic_bookmarkborder),
                    tint = green500,
                    contentDescription = "Salvar",
                    onClick = {}
                )
            }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AetherTopBarDarkPreview() {
    AetherTheme(darkTheme = true) {
        AetherTopBar(title = "Apenas voltar", onBackClick = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AetherTopBarWithTrailingDarkPreview() {
    AetherTheme(darkTheme = true) {
        AetherTopBar(
            title = "Conteúdo fulano",
            onBackClick = {},
            trailingContent = {
                AetherTrailingIconButton(
                    painter = painterResource(id = R.drawable.ic_bookmarkborder),
                    tint = green500,
                    contentDescription = "Salvar",
                    onClick = {}
                )
            }
        )
    }
}