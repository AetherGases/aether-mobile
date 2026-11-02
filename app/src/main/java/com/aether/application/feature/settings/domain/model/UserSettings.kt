package com.aether.application.feature.settings.domain.model

enum class ThemePreference { SYSTEM, LIGHT, DARK }

data class UserSettings(
    val theme: ThemePreference = ThemePreference.SYSTEM,
)
