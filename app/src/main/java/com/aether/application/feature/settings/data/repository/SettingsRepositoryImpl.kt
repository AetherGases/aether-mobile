package com.aether.application.feature.settings.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aether.application.feature.settings.domain.model.ThemePreference
import com.aether.application.feature.settings.domain.model.UserSettings
import com.aether.application.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Device-level preferences, kept in the shared DataStore under their own keys so they
 * survive logout (the session storage only removes its own key).
 */
class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object Keys {
        val NOTIFICATIONS = booleanPreferencesKey("settings_notifications")
        val EMAIL_NOTIFICATIONS = booleanPreferencesKey("settings_email_notifications")
        val THEME = stringPreferencesKey("settings_theme")
    }

    override val settings: Flow<UserSettings> = dataStore.data.map { prefs ->
        val defaults = UserSettings()
        UserSettings(
            notificationsEnabled = prefs[Keys.NOTIFICATIONS] ?: defaults.notificationsEnabled,
            emailNotificationsEnabled = prefs[Keys.EMAIL_NOTIFICATIONS]
                ?: defaults.emailNotificationsEnabled,
            theme = prefs[Keys.THEME]
                ?.let { name -> ThemePreference.entries.firstOrNull { it.name == name } }
                ?: defaults.theme
        )
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.NOTIFICATIONS] = enabled }
    }

    override suspend fun setEmailNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.EMAIL_NOTIFICATIONS] = enabled }
    }

    override suspend fun setTheme(theme: ThemePreference) {
        dataStore.edit { it[Keys.THEME] = theme.name }
    }
}
