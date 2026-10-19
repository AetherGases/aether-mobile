package com.aether.application.feature.chatbot.domain.repository

import com.aether.application.feature.chatbot.domain.model.Conversation

/** Conversations the user bookmarked, kept on this device and separated by account. */
interface SavedConversationsRepository {

    suspend fun getSaved(): List<Conversation>

    suspend fun save(conversation: Conversation)

    suspend fun remove(conversationId: String)
}
