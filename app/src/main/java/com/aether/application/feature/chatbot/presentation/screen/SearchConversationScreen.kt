package com.aether.application.feature.aeko.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.R
import com.aether.core.ui.theme.*

data class ChatHistoryItem(
    val id: String,
    val title: String,
    val timestamp: String
)

@Composable
fun AekoSearchConversationsScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    conversations: List<ChatHistoryItem>,
    onConversationClick: (String) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundLight)
            .drawBehind {
                val purpleRadius = size.width
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(purple500.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = purpleRadius
                    ),
                    radius = purpleRadius,
                    center = Offset(0f, 0f)
                )
                val greenRadius = size.width * 0.9f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(green500.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(size.width, size.height * 0.1f),
                        radius = greenRadius
                    ),
                    radius = greenRadius,
                    center = Offset(size.width, size.height * 0.1f)
                )

                val step = 32.dp.toPx()
                val gridColor = Color.White.copy(alpha = 0.8f)

                var x = 0f
                while (x < size.width) {
                    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                    x += step
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                    y += step
                }
            }
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Surface(
            shape = RoundedCornerShape(100),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(100),
                    ambientColor = purple700.copy(alpha = 0.15f),
                    spotColor = purple700.copy(alpha = 0.15f)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Pesquisar",
                    tint = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.width(12.dp))

                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    textStyle = bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Pesquisar conversa",
                                style = bodyLarge,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(conversations, key = { it.id }) { item ->
                HistoryConversationCard(
                    title = item.title,
                    timestamp = item.timestamp,
                    onClick = { onConversationClick(item.id) }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(50))
                .background(Brush.linearGradient(listOf(Color(0xFF7C5CFC), purple500)))
                .clickable(onClick = onBackClicked),
            contentAlignment = Alignment.Center
        ) {
            Text("Voltar", style = titleMediumMD, color = Color.White)
        }
    }
}

@Composable
private fun HistoryConversationCard(
    title: String,
    timestamp: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = purple700.copy(alpha = 0.08f),
                spotColor = purple700.copy(alpha = 0.08f)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chat),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = timestamp,
                    style = bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun AekoSearchConversationsScreenPreview() {
    AetherTheme {
        AekoSearchConversationsScreen(
            searchQuery = "",
            onSearchQueryChange = {},
            conversations = listOf(
                ChatHistoryItem("1", "Conversa bobinha", "Ontem às 23:20"),
                ChatHistoryItem("2", "Conversa bobinha", "Ontem às 23:20")
            ),
            onConversationClick = {},
            onBackClicked = {}
        )
    }
}