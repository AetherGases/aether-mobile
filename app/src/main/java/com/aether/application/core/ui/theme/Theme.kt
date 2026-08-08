package com.aether.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Typography
import com.aether.core.ui.theme.*

// Sistema de cores

private val LightColors = lightColorScheme(
    primary = purple500,
    onPrimary = textPrimaryDark,
    secondary = green500,
    onSecondary = textPrimaryDark,
    background = backgroundLightElevated,
    surface = backgroundLight,
    onBackground = textPrimaryLight,
    onSurface = textPrimaryLight,
    error = darkRed
)

private val DarkColors = darkColorScheme(
    primary = purple500,
    onPrimary = textPrimaryDark,
    secondary = green500,
    onSecondary = textPrimaryDark,
    background = backgroundDarkElevated,
    surface =   backgroundDark,
    onBackground = textPrimaryDark,
    onSurface = textPrimaryDark,
    error = lightRed
)

// Tipografia

val AetherTypography = Typography(
    displayLarge = displayLarge,
    displayMedium = displayMedium,
    titleLarge = titleLarge,
    titleMedium = titleMedium,
    titleSmall = titleSmall,
    bodyLarge = bodyLarge,
    bodyMedium = bodyMedium,
    bodySmall = bodySmall,
    labelLarge = labelLarge,
    labelMedium = labelMedium,
    labelSmall = labelSmall
)

@Composable
fun AetherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
){
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = AetherTypography,
        content = content
    )
}