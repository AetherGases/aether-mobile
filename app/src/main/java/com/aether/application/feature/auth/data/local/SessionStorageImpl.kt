package com.aether.application.feature.auth.data.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aether.application.core.auth.data.SessionStorage
import com.aether.application.core.auth.model.Session
import com.aether.application.core.database.ProfileLocalDataSource
import com.aether.application.core.security.Encryptor
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private const val TAG = "SessionStorage"

/**
 * Keeps the tokens in the encrypted DataStore. Profile data (name, avatar, permissions)
 * lives in SQLite through [ProfileLocalDataSource] and is merged back on [get].
 */
class SessionStorageImpl(
    private val dataStore: DataStore<Preferences>,
    private val encryptor: Encryptor,
    private val profileLocalDataSource: ProfileLocalDataSource
) : SessionStorage {

    private object Keys {
        val SESSION = stringPreferencesKey("encrypted_session")
    }

    override suspend fun save(session: Session) {
        val tokensOnly = Session(
            email = session.email,
            accessToken = session.accessToken,
            refreshToken = session.refreshToken,
            expiration = session.expiration
        )
        val json = Json.encodeToString(tokensOnly)
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
            val session = Json.decodeFromString<Session>(json)
            val profile = profileLocalDataSource.getProfile()
            if (profile == null) {
                Log.w(TAG, "No stored profile, restoring the session without permissions")
                return session
            }
            Log.d(TAG, "Restored profile ${profile.id} with permissions ${profile.permissions.map { it.name }}")
            session.copy(
                name = profile.name,
                avatar = profile.avatarUrl.orEmpty(),
                permissions = profile.permissions,
                createdAt = profile.createdAt
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to restore session, clearing it", e)
            clear()
            null
        }
    }

    override suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.SESSION)
        }
        profileLocalDataSource.clear()
    }
}
