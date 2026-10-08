package com.aether.application.feature.aeko.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aether.core.ui.theme.*
import com.aether.application.R

data class SavedConversation(
    val id: String,
    val title: String,
    val isSelected: Boolean = false
)

@Composable
fun AekoConversationsListScreen(
    userName: String,
    userRole: String,
    avatarUrl: String?,
    savedConversations: List<SavedConversation>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onNewConversationClick: () -> Unit,
    onConversationClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(purple300, purple500))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_aeko),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text("Aeko", style = titleLarge, fontWeight = FontWeight.Bold, color = textPrimaryLight)
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNewConversationClick)
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = purple500,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text("Nova conversa", style = bodyMedium, fontWeight = FontWeight.Bold, color = textPrimaryLight)
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text("Pesquisar conversa") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = textTertiaryLight
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(percent = 50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                unfocusedContainerColor = textDisabledLight.copy(alpha = 0.08f),
                focusedContainerColor = textDisabledLight.copy(alpha = 0.08f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            "Conversas salvas",
            style = bodySmall,
            fontWeight = FontWeight.Bold,
            color = textTertiaryLight
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(savedConversations, key = { it.id }) { conversation ->
                ConversationRow(
                    conversation = conversation,
                    onClick = { onConversationClick(conversation.id) }
                )
                HorizontalDivider(color = textDisabledLight.copy(alpha = 0.3f))
            }
        }

        HorizontalDivider(color = textDisabledLight.copy(alpha = 0.3f))
        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(userName, style = bodyMedium, fontWeight = FontWeight.Bold, color = textPrimaryLight)
                Text(userRole, style = bodySmall, color = textTertiaryLight)
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configurações",
                    tint = textTertiaryLight
                )
            }
        }
    }
}

@Composable
private fun ConversationRow(
    conversation: SavedConversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(50))
                .background(if (conversation.isSelected) green500 else Color.Transparent)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            conversation.title,
            style = bodyMedium,
            fontWeight = if (conversation.isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textPrimaryLight
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun AekoConversationsListScreenPreview() {
    AetherTheme {
        AekoConversationsListScreen(
            userName = "Daniel Sagaz",
            userRole = "Funcionário",
            avatarUrl = null,
            savedConversations = listOf(
                SavedConversation("1", "Funcionamento do Aether", isSelected = true),
                SavedConversation("2", "Funcionamento do Aether"),
                SavedConversation("3", "Funcionamento do Aether"),
                SavedConversation("4", "Funcionamento do Aether"),
                SavedConversation("5", "Funcionamento do Aether")
            ),
            searchQuery = "",
            onSearchQueryChanged = {},
            onNewConversationClick = {},
            onConversationClick = {},
            onSettingsClick = {}
        )
    }
}