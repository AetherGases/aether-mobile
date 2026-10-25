package com.aether.application.feature.chatbot.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.chatbot.domain.repository.SavedConversationsRepository
import kotlinx.coroutines.flow.first

private const val KEY_PREFIX = "chatbot_pinned_"

/**
 * Keeps the ids of the pinned conversations in the shared DataStore, one entry per account
 * email, so they don't leak between users of the same device. Logout wipes them all. The
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

    override suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.asMap().keys.filter { it.name.startsWith(KEY_PREFIX) }.forEach { prefs.remove(it) }
        }
    }

    private fun key() = sessionManager.getSession()?.email
        ?.let { stringSetPreferencesKey("$KEY_PREFIX$it") }
}
