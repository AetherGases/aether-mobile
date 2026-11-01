package com.aether.application.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** A card of the home overview pager. */
sealed interface HomeHeroCard {
    @Composable
    fun Render(modifier: Modifier = Modifier)
}
