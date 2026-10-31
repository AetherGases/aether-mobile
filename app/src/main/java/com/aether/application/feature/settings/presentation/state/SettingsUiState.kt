package com.aether.application.feature.settings.presentation.state

import com.aether.application.R
import com.aether.application.core.utils.UiText
import com.aether.application.core.utils.uiText
import com.aether.application.feature.settings.domain.model.ThemePreference

data class SettingsUiState(
    val userName: String = "",
    val avatarUrl: String? = null,
    val isUploadingAvatar: Boolean = false,
    /** Shown under the header until the next upload attempt. */
    val avatarErrorMessage: UiText? = null,
    val reportsCount: Int = 0,
    val entryDate: String = "",
    val notificationsEnabled: Boolean = true,
    val emailNotificationsEnabled: Boolean = true,
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val canTalkToAeko: Boolean = false,
)
