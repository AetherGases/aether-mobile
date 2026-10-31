package com.aether.application.feature.chatbot.presentation.state

import androidx.annotation.StringRes
import com.aether.application.R
import com.aether.application.core.utils.UiText
import com.aether.application.core.utils.uiText
import com.aether.application.feature.chatbot.domain.model.Conversation
import com.aether.application.feature.chatbot.presentation.screen.ConversationItem
import com.aether.core.ui.components.ChatMessage
import com.aether.core.ui.components.ChatSender

private const val TYPING_MESSAGE_ID = "typing"

data class ChatbotUiState(
    val userName: String = "",
    val userRole: UiText = uiText(""),
    val avatarUrl: String? = null,
    val title: UiText = uiText(R.string.chatbot_new_conversation),
    val messages: List<ChatMessage> = emptyList(),
    @StringRes val suggestionRes: List<Int> = emptyList(),
    val inputText: String = "",
    val conversations: List<Conversation> = emptyList(),
    val pinnedConversationIds: Set<String> = emptySet(),
    val conversationSearchQuery: String = "",
    val activeConversationId: String? = null,
    val isSending: Boolean = false,
    val isLoadingConversation: Boolean = false,
    val errorMessage: UiText? = null,
) {
    /** The messages plus a placeholder from the assistant while it is answering. */
    val displayMessages: List<ChatMessage>
        get() = if (isSending) {
            messages + ChatMessage(TYPING_MESSAGE_ID, ChatSender.AEKO, "", isTyping = true)
        } else {
            messages
        }

    val isConversationPinned: Boolean
        get() = activeConversationId in pinnedConversationIds

    /** Every conversation matching the search, pinned ones first, otherwise in the hub's order. */
    val visibleConversations: List<ConversationItem>
        get() = conversations
            .filter { it.name.contains(conversationSearchQuery.trim(), ignoreCase = true) }
            .sortedByDescending { it.id in pinnedConversationIds }
            .map {
                ConversationItem(
                    id = it.id,
                    title = it.name,
                    isPinned = it.id in pinnedConversationIds,
                    isSelected = it.id == activeConversationId
                )
            }
}
