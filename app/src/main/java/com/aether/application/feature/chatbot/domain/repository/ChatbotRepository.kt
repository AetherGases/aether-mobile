package com.aether.application.feature.chatbot.domain.repository

import com.aether.application.feature.chatbot.domain.model.ChatReply
import com.aether.application.feature.chatbot.domain.model.ChatTurn
import com.aether.application.feature.chatbot.domain.model.Conversation

interface ChatbotRepository {

    /** Every session the logged-in user has with the assistant. */
    suspend fun getConversations(): List<Conversation>

    suspend fun getMessages(conversationId: String): List<ChatTurn>

    /** Sends [input] to [conversationId], or starts a new conversation when it is null. */
    suspend fun sendMessage(conversationId: String?, input: String): ChatReply
}
