package com.aether.application.feature.chatbot.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.core.ui.components.AetherTopBar
import com.aether.application.core.ui.components.AetherTrailingIconButton
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
    isConversationPinned: Boolean,
    conversations: List<ConversationItem>,
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

    val listState = rememberLazyListState()
    val currentMessages by rememberUpdatedState(messages)
    val showScrollToBottom by remember { derivedStateOf { listState.canScrollForward } }
    var typingDotsVisible by remember { mutableStateOf(false) }
    var lastSeenFirstId by remember { mutableStateOf<String?>(null) }

    // Only tracked while the placeholder exists, since it is gone by the time the reply arrives
    LaunchedEffect(listState) {
        snapshotFlow {
            val typingIndex = currentMessages.indexOfLast { it.isTyping }
            if (typingIndex < 0) null
            else listState.layoutInfo.visibleItemsInfo.any { it.index == typingIndex }
        }.collect { visible -> if (visible != null) typingDotsVisible = visible }
    }

    val firstId = messages.firstOrNull()?.id
    val last = messages.lastOrNull()
    LaunchedEffect(firstId, last?.id) {
        if (last == null) {
            lastSeenFirstId = null
            return@LaunchedEffect
        }
        val lastIndex = messages.lastIndex
        when {
            firstId != lastSeenFirstId -> listState.scrollToItem(lastIndex)
            last.sender == ChatSender.USER || last.isTyping -> listState.animateScrollToItem(lastIndex)
            typingDotsVisible -> listState.animateScrollToItem(lastIndex)
        }
        lastSeenFirstId = firstId
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surface) {
                AekoConversationsListScreen(
                    userName = userName,
                    userRole = userRole,
                    avatarUrl = avatarUrl,
                    conversations = conversations,
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
        // Salvando as cores fora do bloco drawBehind para evitar o erro de contexto @Composable
        val primaryColor = MaterialTheme.colorScheme.primary
        val secondaryColor = MaterialTheme.colorScheme.secondary
        val surfaceColor = MaterialTheme.colorScheme.surface

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .drawBehind {
                    val purpleRadius = size.width
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(primaryColor.copy(alpha = 0.28f), Color.Transparent),
                            center = Offset(0f, 0f),
                            radius = purpleRadius
                        ),
                        radius = purpleRadius,
                        center = Offset(0f, 0f)
                    )

                    val greenRadius = size.width * 0.9f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(secondaryColor.copy(alpha = 0.20f), Color.Transparent),
                            center = Offset(size.width, size.height * 0.1f),
                            radius = greenRadius
                        ),
                        radius = greenRadius,
                        center = Offset(size.width, size.height * 0.1f)
                    )

                    val step = 32.dp.toPx()
                    val gridColor = surfaceColor.copy(alpha = 0.8f)

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
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AetherTrailingIconButton(
                            painter = rememberVectorPainter(Icons.Default.Menu),
                            contentDescription = "Abrir conversas salvas",
                            onClick = { coroutineScope.launch { drawerState.open() } }
                        )
                        AetherTrailingIconButton(
                            painter = painterResource(id = if (isConversationPinned) R.drawable.ic_bookmark else R.drawable.ic_bookmarkborder),
                            tint = MaterialTheme.colorScheme.secondary,
                            contentDescription = if (isConversationPinned) "Remover dos salvos" else "Salvar conversa",
                            onClick = onBookmarkClicked
                        )
                    }
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

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_aeko_mascot),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(240.dp)
                                .offset(x = 15.dp)
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
                            rowSuggestions.forEachIndexed { index, suggestion ->
                                SuggestionChip(
                                    text = suggestion,
                                    onClick = { onSuggestionClicked(suggestion) }
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
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
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

                    ScrollToBottomButton(
                        visible = showScrollToBottom,
                        onClick = {
                            coroutineScope.launch {
                                listState.animateScrollToItem(messages.lastIndex)
                                listState.animateScrollBy(100_000f)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 20.dp, bottom = 8.dp)
                    )
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
private fun ScrollToBottomButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        modifier = modifier
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 6.dp,
            onClick = onClick
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_down),
                contentDescription = "Ir para o fim da conversa",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(10.dp).size(24.dp)
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
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(100),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
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
            isConversationPinned = false,
            conversations = listOf(
                ConversationItem("1", "Funcionamento do Aether", isPinned = true, isSelected = true),
                ConversationItem("2", "Funcionamento do Aether")
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
            isConversationPinned = true,
            conversations = listOf(
                ConversationItem("1", "Funcionamento do Aether", isPinned = true, isSelected = true),
                ConversationItem("2", "Funcionamento do Aether")
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