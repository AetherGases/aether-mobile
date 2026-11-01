package com.aether.application.core.ui.components

import com.aether.application.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.core.ui.theme.*

enum class NotificationStatus {APROVADO, REPROVADO, PENDENTE}

@Composable
fun NotificationCard(
    status: NotificationStatus,
    title: String,
    description: String,
    timestamp: String,
    relativeTime: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val iconResId = when (status) {
        NotificationStatus.APROVADO -> R.drawable.ic_approved_report
        NotificationStatus.REPROVADO -> R.drawable.ic_rejected_report
        NotificationStatus.PENDENTE -> R.drawable.ic_inventory
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                spotColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(18.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(modifier = Modifier.padding(top = 4.dp)) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(2.dp))
            Text(description, style = bodySmallMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(2.dp))
            Text(timestamp, style = bodySmallMedium, color = MaterialTheme.colorScheme.outlineVariant)
        }

        Text(relativeTime, style = bodySmallMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.outline)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F7)
@Composable
fun NotificationCardPreview() {
    AetherTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            NotificationCard(
                status = NotificationStatus.APROVADO,
                title = "Relatório aprovado",
                description = "Revisado por Nisflei Grandão",
                timestamp = "Hoje 09:12",
                relativeTime = "Há 2 horas"
            )
            Spacer(Modifier.height(12.dp))
            NotificationCard(
                status = NotificationStatus.REPROVADO,
                title = "Relatório reprovado",
                description = "Revisado por Nisflei Grandão",
                timestamp = "Hoje 09:12",
                relativeTime = "Há 2 horas"
            )
            Spacer(Modifier.height(12.dp))
            NotificationCard(
                status = NotificationStatus.PENDENTE,
                title = "Relatório recebido",
                description = "Realizado por Daniel Sagaz",
                timestamp = "Hoje 09:12",
                relativeTime = "Há 2 horas"
            )
        }
    }
}