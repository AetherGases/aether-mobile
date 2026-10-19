package com.aether.application.feature.chatbot.presentation.state

import com.aether.application.feature.chatbot.domain.model.Conversation
import com.aether.application.feature.chatbot.presentation.screen.SavedConversation
import com.aether.core.ui.components.ChatMessage
import com.aether.core.ui.components.ChatSender

const val NEW_CONVERSATION_TITLE = "Nova conversa"
private const val TYPING_MESSAGE_ID = "typing"

data class ChatbotUiState(
    val userName: String = "",
    val userRole: String = "",
    val avatarUrl: String? = null,
    val title: String = NEW_CONVERSATION_TITLE,
    val messages: List<ChatMessage> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val inputText: String = "",
    val savedConversations: List<Conversation> = emptyList(),
    val conversationSearchQuery: String = "",
    val activeConversationId: String? = null,
    val isSending: Boolean = false,
    val isLoadingConversation: Boolean = false,
    val errorMessage: String? = null,
) {
    /** The messages plus a placeholder from the assistant while it is answering. */
    val displayMessages: List<ChatMessage>
        get() = if (isSending) {
            messages + ChatMessage(TYPING_MESSAGE_ID, ChatSender.AEKO, "Digitando…")
        } else {
            messages
        }

    val isConversationSaved: Boolean
        get() = savedConversations.any { it.id == activeConversationId }

    val visibleConversations: List<SavedConversation>
        get() = savedConversations
            .filter { it.name.contains(conversationSearchQuery.trim(), ignoreCase = true) }
            .map { SavedConversation(it.id, it.name, isSelected = it.id == activeConversationId) }
}
