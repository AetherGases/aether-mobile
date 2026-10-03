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

    // roxos

    primary = purple500,
    onPrimary = backgroundLightElevated,// (texto)
    primaryContainer = purple100,
    onPrimaryContainer = purple900,
    inversePrimary = purple300,

    // Verdes

    secondary = green500,
    onSecondary = backgroundLightElevated,
    secondaryContainer = green100,
    onSecondaryContainer = green900,

    // Semânticas

    tertiary = darkYellow,
    onTertiary = backgroundLightElevated,
    tertiaryContainer = lightYellow,
    onTertiaryContainer = darkYellow,

    error = darkRed,
    onError = backgroundLightElevated,
    errorContainer = lightRed,
    onErrorContainer = darkRed,

    // Fundos

    background = backgroundLight,
    onBackground = textPrimaryLight, //(texto)

    // Superfícies

    surface = backgroundLightElevated,
    onSurface = textPrimaryLight, // (texto)
    surfaceVariant = backgroundLight,
    onSurfaceVariant = textSecondaryLight,
    surfaceTint = purple500,
    inverseSurface = backgroundDark,
    inverseOnSurface = textPrimaryDark,

    // Bordas e linhas

    outline = textTertiaryLight,
    outlineVariant = textDisabledLight,


)

private val DarkColors = darkColorScheme(

    // Roxos

    primary = purple500,
    onPrimary = textPrimaryDark, // texto)
    primaryContainer = purple700,
    onPrimaryContainer = purple100,
    inversePrimary = purple500,

    // Verdes

    secondary = green500,
    onSecondary = textPrimaryDark,// texto
    secondaryContainer = green700,
    onSecondaryContainer = green100,

    // Semânticas

    tertiary = lightYellow,
    onTertiary = backgroundDark, // (texto)
    tertiaryContainer = darkYellow,
    onTertiaryContainer = lightYellow,

    error = lightRed,
    onError = textPrimaryDark,
    errorContainer = darkRed,
    onErrorContainer = lightRed,


    // Fundos

    background = backgroundDark,
    onBackground = textPrimaryDark, //  texto

    // Superfícies

    surface = backgroundDarkElevated,
    onSurface = textPrimaryDark, //texto
    surfaceVariant = backgroundDark,
    onSurfaceVariant = textSecondaryDark,//texto
    surfaceTint = purple500,
    inverseSurface = backgroundLightElevated,
    inverseOnSurface = textPrimaryLight,


    // Bordas e Linhas

    outline = textTertiaryDark,
    outlineVariant = textDisabledDark,
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