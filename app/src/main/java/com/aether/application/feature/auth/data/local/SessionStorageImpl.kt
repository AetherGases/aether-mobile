package com.aether.application.feature.auth.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aether.application.core.auth.data.SessionStorage
import com.aether.application.core.auth.model.Session
import com.aether.application.core.security.Encryptor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class SessionStorageImpl(
    private val dataStore: DataStore<Preferences>,
    private val encryptor: Encryptor
) : SessionStorage {

    private object Keys {
        val SESSION = stringPreferencesKey("encrypted_session")
    }

    override suspend fun save(session: Session) {
        val json = Json.encodeToString(session)
        val encrypted = encryptor.encrypt(json)
        dataStore.edit { prefs ->
            prefs[Keys.SESSION] = encrypted
        }
    }

    override suspend fun get(): Session? {
        val encrypted = dataStore.data
            .map { it[Keys.SESSION] }
            .first() ?: return null

        return try {
            val json = encryptor.decrypt(encrypted)
            Json.decodeFromString<Session>(json)
        } catch (e: Exception) {
            clear()
            null
        }
    }

    override suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.SESSION)
        }
    }
}