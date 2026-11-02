package com.aether.application.feature.settings.presentation.state

import com.aether.application.feature.settings.domain.model.ThemePreference

data class SettingsUiState(
    val userName: String = "",
    val avatarUrl: String? = null,
    val isUploadingAvatar: Boolean = false,
    /** Shown under the header until the next upload attempt. */
    val avatarErrorMessage: String? = null,
    val reportsCount: Int = 0,
    /** Only set for someone who both makes and reviews reports; [reportsCount] is then the ones made. */
    val reviewedCount: Int? = null,
    val entryDate: String = "",
    val notificationsEnabled: Boolean = true,
    val emailNotificationsEnabled: Boolean = true,
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val canTalkToAeko: Boolean = false,
    // TODO: no translated resources yet, so the language is fixed
    val languageLabel: String = "Português (BR)",
)
