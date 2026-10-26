package com.aether.application.feature.settings.presentation.state

import com.aether.application.feature.settings.domain.model.ThemePreference

data class SettingsUiState(
    val userName: String = "",
    val avatarUrl: String? = null,
    val isUploadingAvatar: Boolean = false,
    /** One-shot message, cleared with SettingsViewModel.onAvatarErrorShown. */
    val avatarErrorMessage: String? = null,
    val reportsCount: Int = 0,
    val entryDate: String = "",
    val notificationsEnabled: Boolean = true,
    val emailNotificationsEnabled: Boolean = true,
    val theme: ThemePreference = ThemePreference.SYSTEM,
    // TODO: no translated resources yet, so the language is fixed
    val languageLabel: String = "Português (BR)",
)
