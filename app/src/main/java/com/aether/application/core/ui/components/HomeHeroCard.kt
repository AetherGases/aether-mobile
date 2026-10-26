package com.aether.core.ui.components

import androidx.compose.runtime.Composable

/** A card of the home overview pager. */
sealed interface HomeHeroCard {
    @Composable
    fun Render()
}
