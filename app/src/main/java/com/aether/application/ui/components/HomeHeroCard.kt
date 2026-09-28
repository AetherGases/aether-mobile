package com.aether.core.ui.components

import androidx.compose.runtime.Composable

sealed interface HomeHeroCard {
    val lastSubmittedLabel: String
    val summaryTitle: String
    val summaryCount: String

    @Composable
    fun Render()
}
