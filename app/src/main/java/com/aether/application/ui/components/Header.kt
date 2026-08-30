package com.aether.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aether.application.R
import com.aether.core.ui.theme.*

@Composable
fun AppHeader(
    userName: String,
    avatarUrl: String?,
    hasUnreadNotifications: Boolean,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = userName,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text("Seja bem-vindo,", style = bodySmallMedium, color = textTertiaryLight)
            Text(userName, style = labelLarge, color = textPrimaryLight) }

        Box {
           IconButton(onClick = onNotificationsClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bell),
                    contentDescription = "Notificações",
                    tint = textTertiaryLight
                )
            }
            if (hasUnreadNotifications) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                        .offset(y = 8.dp, x = (-12).dp)
                        .clip(CircleShape)
                        .background(lightRed)
                )
            }
        }

        IconButton(onClick = onSettingsClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = "Configurações",
                tint = textTertiaryLight
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppHeaderPreview() {
    AetherTheme {
        AppHeader(
            userName = "Daniel Sagaz",
            avatarUrl = null,
            hasUnreadNotifications = true,
            onNotificationsClick = {},
            onSettingsClick = {}
        )
    }
}