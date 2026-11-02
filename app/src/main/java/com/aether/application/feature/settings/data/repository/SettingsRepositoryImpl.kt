package com.aether.application.feature.settings.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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
        val THEME = stringPreferencesKey("settings_theme")
    }

    override val settings: Flow<UserSettings> = dataStore.data.map { prefs ->
        val defaults = UserSettings()
        UserSettings(
            theme = prefs[Keys.THEME]
                ?.let { name -> ThemePreference.entries.firstOrNull { it.name == name } }
                ?: defaults.theme
        )
    }

    override suspend fun setTheme(theme: ThemePreference) {
        dataStore.edit { it[Keys.THEME] = theme.name }
    }
}
