package com.aether.application.feature.chatbot.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.chatbot.domain.model.Conversation
import com.aether.application.feature.chatbot.domain.repository.SavedConversationsRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * Keeps the bookmarked conversations in the shared DataStore, one entry per account email, so
 * they survive logout and don't leak between users of the same device.
 */
class SavedConversationsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val sessionManager: SessionManager
) : SavedConversationsRepository {

    @Serializable
    private data class Stored(val id: String, val name: String)

    override suspend fun getSaved(): List<Conversation> {
        val key = key() ?: return emptyList()
        return read(dataStore.data.first()[key])
    }

    override suspend fun save(conversation: Conversation) {
        val key = key() ?: return
        dataStore.edit { prefs ->
            val others = read(prefs[key]).filterNot { it.id == conversation.id }
            // Newest first
            write(prefs, key, listOf(conversation) + others)
        }
    }

    override suspend fun remove(conversationId: String) {
        val key = key() ?: return
        dataStore.edit { prefs ->
            write(prefs, key, read(prefs[key]).filterNot { it.id == conversationId })
        }
    }

    private fun key() = sessionManager.getSession()?.email
        ?.let { stringPreferencesKey("chatbot_saved_$it") }

    private fun read(json: String?): List<Conversation> {
        if (json == null) return emptyList()
        return try {
            Json.decodeFromString<List<Stored>>(json).map { Conversation(it.id, it.name) }
        } catch (_: SerializationException) {
            emptyList()
        }
    }

    private fun write(
        prefs: androidx.datastore.preferences.core.MutablePreferences,
        key: Preferences.Key<String>,
        conversations: List<Conversation>
    ) {
        prefs[key] = Json.encodeToString(conversations.map { Stored(it.id, it.name) })
    }
}
