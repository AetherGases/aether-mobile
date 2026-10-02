package com.aether.application.feature.home.presentation.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class QuickAction(
    val label: String,
    val iconRes: Int,
    val iconSize: Dp = 28.dp,
    val offsetX: Dp = 0.dp,
    val offsetY: Dp = 0.dp,
    val backgroundColor: Color,
    val onClick: () -> Unit
)