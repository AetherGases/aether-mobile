package com.aether.application.core.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aether.application.R
import com.aether.core.ui.theme.AetherTheme

@Composable
fun AetherTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val titleColor = MaterialTheme.colorScheme.onBackground

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
) {
    ThemeAwareIconButton(
        painter = painter,
        contentDescription = contentDescription,
        onClick = onClick,
        iconSize = 24.dp,
        modifier = modifier,
    )
}

@Composable
private fun ThemeAwareIconButton(
    painter: Painter,
    contentDescription: String?,
    onClick: () -> Unit,
    iconSize: Dp,
    modifier: Modifier = Modifier,
) {
    var buttonModifier = modifier.size(44.dp)

    buttonModifier = buttonModifier
            .shadow(
                elevation = 10.dp,
                shape = CircleShape,
                ambientColor = MaterialTheme.colorScheme.background.copy(alpha = 0.32f),
                spotColor = MaterialTheme.colorScheme.background.copy(alpha = 0.28f)
            )
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                ),
                shape = CircleShape
            )
            .border(
                border = BorderStroke(
                    0.8.dp,
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.outline,
                            MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                ),
                shape = CircleShape
            )

    IconButton(
        onClick = onClick,
        modifier = buttonModifier
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onBackground,
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
                    contentDescription = "Salvar",
                    onClick = {}
                )
            }
        )
    }
}