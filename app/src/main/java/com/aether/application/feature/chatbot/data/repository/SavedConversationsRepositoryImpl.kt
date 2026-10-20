package com.aether.application.feature.chatbot.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.chatbot.domain.repository.SavedConversationsRepository
import kotlinx.coroutines.flow.first

/**
 * Keeps the ids of the pinned conversations in the shared DataStore, one entry per account
 * email, so they survive logout and don't leak between users of the same device. The
 * conversations themselves (and their names) come from the hub.
 */
class SavedConversationsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val sessionManager: SessionManager
) : SavedConversationsRepository {

    override suspend fun getPinnedIds(): Set<String> {
        val key = key() ?: return emptySet()
        return dataStore.data.first()[key].orEmpty()
    }

    override suspend fun pin(conversationId: String) {
        val key = key() ?: return
        dataStore.edit { it[key] = it[key].orEmpty() + conversationId }
    }

    override suspend fun unpin(conversationId: String) {
        val key = key() ?: return
        dataStore.edit { it[key] = it[key].orEmpty() - conversationId }
    }

    private fun key() = sessionManager.getSession()?.email
        ?.let { stringSetPreferencesKey("chatbot_pinned_$it") }
}
