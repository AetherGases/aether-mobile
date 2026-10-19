package com.aether.application.feature.chatbot.domain.repository

import com.aether.application.feature.chatbot.domain.model.ChatReply
import com.aether.application.feature.chatbot.domain.model.ChatTurn

interface ChatbotRepository {

    suspend fun getMessages(conversationId: String): List<ChatTurn>

    /** Sends [input] to [conversationId], or starts a new conversation when it is null. */
    suspend fun sendMessage(conversationId: String?, input: String): ChatReply
}
