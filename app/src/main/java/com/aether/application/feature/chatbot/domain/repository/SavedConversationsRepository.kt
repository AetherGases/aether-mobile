package com.aether.application.feature.chatbot.domain.repository

/** Ids of the conversations the user pinned, kept on this device and separated by account until logout. */
interface SavedConversationsRepository {

    suspend fun getPinnedIds(): Set<String>

    suspend fun pin(conversationId: String)

    suspend fun unpin(conversationId: String)

    /** Forgets the pins of every account. */
    suspend fun clear()
}
