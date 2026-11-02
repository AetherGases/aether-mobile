package com.aether.application.core.notification.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aether.application.core.notification.domain.FcmTokenStorage
import kotlinx.coroutines.flow.first

class FcmTokenStorageImpl(
    private val dataStore: DataStore<Preferences>
): FcmTokenStorage {
    companion object {
        private val FCM_TOKEN = stringPreferencesKey("fcm_token")
    }

    override suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[FCM_TOKEN] = token
        }
    }

    override suspend fun getToken(): String? {
        return dataStore.data.first()[FCM_TOKEN]
    }
}