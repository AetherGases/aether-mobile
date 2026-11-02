package com.aether.application.feature.home.presentation.components

import androidx.annotation.StringRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class QuickActionContainer { PRIMARY, SECONDARY }

data class QuickAction(
    @StringRes val labelRes: Int,
    val iconRes: Int,
    val iconSize: Dp = 28.dp,
    val offsetX: Dp = 0.dp,
    val offsetY: Dp = 0.dp,
    val container: QuickActionContainer,
    val onClick: () -> Unit
)