package com.aether.application.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.aether.application.core.ui.theme.labelMedium

/**
 * Inline error shown as "* message", in place of a pop-up. Same look as the login error:
 * colorScheme.error is lightRed in the light theme and also follows the dark theme.
 */
@Composable
fun FormErrorText(message: String, modifier: Modifier = Modifier) {
    Text(
        text = "* $message",
        style = labelMedium,
        color = MaterialTheme.colorScheme.error,
        // Screen readers announce it as soon as it appears, like a pop-up would
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite }
    )
}
