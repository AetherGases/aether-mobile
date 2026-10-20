package com.aether.application.feature.settings.domain.repository

import com.aether.application.feature.settings.domain.model.ThemePreference
import com.aether.application.feature.settings.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val settings: Flow<UserSettings>

    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setEmailNotificationsEnabled(enabled: Boolean)
    suspend fun setTheme(theme: ThemePreference)
}
