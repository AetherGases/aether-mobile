package com.aether.application.feature.aeko.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.core.ui.components.AetherTopBar
import com.aether.core.ui.components.AetherTrailingIconButton
import com.aether.core.ui.components.ChatInputBar
import com.aether.core.ui.components.ChatMessage
import com.aether.core.ui.components.ChatMessageBubble
import com.aether.core.ui.components.ChatSender
import com.aether.core.ui.theme.*
import com.aether.application.R
import kotlinx.coroutines.launch

@Composable
fun AekoScreen(
    screenTitle: String,
    messages: List<ChatMessage>,
    suggestions: List<String>,
    inputText: String,
    isConversationSaved: Boolean,
    savedConversations: List<SavedConversation>,
    conversationSearchQuery: String,
    userName: String,
    userRole: String,
    avatarUrl: String?,
    onInputTextChanged: (String) -> Unit,
    onSuggestionClicked: (String) -> Unit,
    onSendClicked: () -> Unit,
    onBookmarkClicked: () -> Unit,
    onCopyMessage: (String) -> Unit,
    onConversationSearchQueryChanged: (String) -> Unit,
    onNewConversationClick: () -> Unit,
    onConversationClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surface) {
                AekoConversationsListScreen(
                    userName = userName,
                    userRole = userRole,
                    avatarUrl = avatarUrl,
                    savedConversations = savedConversations,
                    searchQuery = conversationSearchQuery,
                    onSearchQueryChanged = onConversationSearchQueryChanged,
                    onNewConversationClick = {
                        onNewConversationClick()
                        coroutineScope.launch { drawerState.close() }
                    },
                    onConversationClick = { id ->
                        onConversationClick(id)
                        coroutineScope.launch { drawerState.close() }
                    },
                    onSettingsClick = onSettingsClick
                )
            }
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(backgroundLight)
                .drawBehind {
                    val purpleRadius = size.width
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(purple500.copy(alpha = 0.28f), Color.Transparent),
                            center = Offset(0f, 0f),
                            radius = purpleRadius
                        ),
                        radius = purpleRadius,
                        center = Offset(0f, 0f)
                    )

                    val greenRadius = size.width * 0.9f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(green500.copy(alpha = 0.20f), Color.Transparent),
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
        ) {
            AetherTopBar(
                title = screenTitle,
                onBackClick = onBackClicked,
                trailingContent = {
                    AetherTrailingIconButton(
                        painter = painterResource(id = if (isConversationSaved) R.drawable.ic_bookmark else R.drawable.ic_bookmarkborder),
                        tint = green500,
                        contentDescription = if (isConversationSaved) "Remover dos salvos" else "Salvar conversa",
                        onClick = onBookmarkClicked
                    )
                }
            )

            if (messages.isEmpty()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(Modifier.weight(1f))
//
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_aeko_mascot),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(240.dp)
                                .offset(x=15.dp)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Olá! Sou o Aeko, seu\nAssistente Virtual.",
                            style = titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(48.dp))

                    suggestions.chunked(2).forEach { rowSuggestions ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            rowSuggestions.forEachIndexed {index, suggestion ->
                                SuggestionChip(
                                    text = suggestion,
                                    onClick = {onSuggestionClicked(suggestion)}
                                )
                                if (index < rowSuggestions.lastIndex) {
                                    Spacer(Modifier.width(16.dp))
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    Spacer(Modifier.weight(1f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(messages, key = { it.id }) { message ->
                        ChatMessageBubble(
                            message = message,
                            onCopyClick = { onCopyMessage(message.id) }
                        )
                    }
                }
            }

            ChatInputBar(
                value = inputText,
                onValueChange = onInputTextChanged,
                onSendClick = onSendClicked,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
            )
        }
    }
}

@Composable
private fun SuggestionChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(100),
        color = Color.White.copy(alpha = 0.8f),
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(100),
                ambientColor = purple700.copy(alpha = 0.3f),
                spotColor = purple700.copy(alpha = 0.4f)
            )
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            style = bodyMediumMd,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun AekoScreenWelcomePreview() {
    AetherTheme {
        AekoScreen(
            screenTitle = "Conteúdo fulano",
            messages = emptyList(),
            suggestions = listOf("Como funciona?", "Como funciona?", "Bla bla bla", "Bla bla bla"),
            inputText = "",
            isConversationSaved = false,
            savedConversations = listOf(
                SavedConversation("1", "Funcionamento do Aether", isSelected = true),
                SavedConversation("2", "Funcionamento do Aether")
            ),
            conversationSearchQuery = "",
            userName = "Daniel Sagaz",
            userRole = "Funcionário",
            avatarUrl = null,
            onInputTextChanged = {},
            onSuggestionClicked = {},
            onSendClicked = {},
            onBookmarkClicked = {},
            onCopyMessage = {},
            onConversationSearchQueryChanged = {},
            onNewConversationClick = {},
            onConversationClick = {},
            onSettingsClick = {},
            onBackClicked = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun AekoScreenConversationPreview() {
    AetherTheme {
        AekoScreen(
            screenTitle = "Funcionamento do app",
            messages = listOf(
                ChatMessage("1", ChatSender.USER, "Eu queria saber como faz as coisa"),
                ChatMessage(
                    "2",
                    ChatSender.AEKO,
                    "Boa tentativa! Uma forma mais natural de se dizer seria: \"Eu gostaria de saber como posso realizar determinada ação\""
                ),
                ChatMessage("3", ChatSender.USER, "Para de ser burra e me ajuda oxi deus"),
                ChatMessage(
                    "4",
                    ChatSender.AEKO,
                    "Opa, o sistema aqui é inteligente, mas ainda não aprendeu a ler mentes! Me diz aí: o que exatamente você quer resolver hoje?"
                )
            ),
            suggestions = emptyList(),
            inputText = "Asquerosa",
            isConversationSaved = true,
            savedConversations = listOf(
                SavedConversation("1", "Funcionamento do Aether", isSelected = true),
                SavedConversation("2", "Funcionamento do Aether")
            ),
            conversationSearchQuery = "",
            userName = "Daniel Sagaz",
            userRole = "Funcionário",
            avatarUrl = null,
            onInputTextChanged = {},
            onSuggestionClicked = {},
            onSendClicked = {},
            onBookmarkClicked = {},
            onCopyMessage = {},
            onConversationSearchQueryChanged = {},
            onNewConversationClick = {},
            onConversationClick = {},
            onSettingsClick = {},
            onBackClicked = {}
        )
    }
}