package com.aether.application.feature.proximity.presentation.qa

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.aether.application.core.ui.theme.titleMediumMD
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Lets QA use the app away from the workplace. Lives in the debug source set only;
 * release ships a no-op twin, so the bypass is never compiled into release builds.
 */
object QaProximityBypass {
    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    /** Lasts until the user leaves the app graph, e.g. on logout. */
    fun reset() {
        _isActive.value = false
    }

    @Composable
    fun Button() {
        TextButton(onClick = { _isActive.value = true }) {
            Text("QA: ignorar proximidade", style = titleMediumMD, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
