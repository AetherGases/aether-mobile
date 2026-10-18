package com.aether.core.ui.components

import androidx.compose.runtime.Composable

/** A card of the home overview pager. */
sealed interface HomeHeroCard {
    @Composable
    fun Render()
}

data class ReportsSummaryHomeCard(
    val title: String,
    val lastSubmittedLabel: String,
    val count: String,
    val onViewHistoryClick: () -> Unit,
) : HomeHeroCard {
    @Composable
    override fun Render() = ReportsSummaryCard(title, lastSubmittedLabel, count, onViewHistoryClick)
}

data class UnitEmissionsHomeCard(
    val value: String,
    val unitLabel: String,
    val changeLabel: String,
) : HomeHeroCard {
    @Composable
    override fun Render() = UnitEmissionsCard(value, unitLabel, changeLabel)
}

data class SealProgressHomeCard(
    val levelPercent: String,
    val criteriaLabel: String,
) : HomeHeroCard {
    @Composable
    override fun Render() = SealProgressCard(levelPercent, criteriaLabel)
}
