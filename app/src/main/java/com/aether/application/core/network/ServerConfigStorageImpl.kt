package com.aether.application.core.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class ServerConfigStorageImpl(
    private val dataStore: DataStore<Preferences>
) : ServerConfigStorage {

    private object Keys {
        val DOMAINS = stringPreferencesKey("qa_server_domains")
    }

    override suspend fun getSavedDomains(): List<String> {
        val json = dataStore.data.map { it[Keys.DOMAINS] }.first() ?: return emptyList()
        return decodeDomains(json)
    }

    override fun observeSelectedDomain(): Flow<String?> =
        dataStore.data.map { prefs ->
            prefs[Keys.DOMAINS]?.let { decodeDomains(it).firstOrNull() }
        }

    override suspend fun saveDomain(domain: String) {
        val normalized = normalize(domain)
        val updated = listOf(normalized) + getSavedDomains().filterNot { it == normalized }

        dataStore.edit { prefs ->
            prefs[Keys.DOMAINS] = Json.encodeToString(updated)
        }
    }

    private fun decodeDomains(json: String): List<String> =
        try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }

    private fun normalize(domain: String): String {
        val trimmed = domain.trim().trimEnd('/')
        return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "http://$trimmed"
        }
    }
}
