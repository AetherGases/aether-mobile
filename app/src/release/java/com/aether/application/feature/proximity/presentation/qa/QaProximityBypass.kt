package com.aether.application.feature.proximity.presentation.qa

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Release twin of the debug-only QA bypass: never active, renders nothing. */
object QaProximityBypass {
    val isActive: StateFlow<Boolean> = MutableStateFlow(false)

    fun reset() = Unit

    @Composable
    fun Button() = Unit
}
