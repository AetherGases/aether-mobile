package com.aether.application.feature.settings.presentation.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.aether.application.R
import com.aether.application.feature.settings.domain.model.ThemePreference
import androidx.compose.foundation.layout.Column

@Composable
fun ThemePreference.label(): String = stringResource(
    when (this) {
        ThemePreference.SYSTEM -> R.string.settings_theme_system
        ThemePreference.LIGHT -> R.string.settings_theme_light
        ThemePreference.DARK -> R.string.settings_theme_dark
    }
)

@Composable
fun ThemePickerDialog(
    selected: ThemePreference,
    onSelect: (ThemePreference) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_theme)) },
        text = {
            Column(modifier = Modifier.selectableGroup()) {
                ThemePreference.entries.forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = theme == selected,
                                onClick = { onSelect(theme) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // The row handles the click, so the button stays out of the way of TalkBack
                        RadioButton(selected = theme == selected, onClick = null)
                        Text(theme.label(), modifier = Modifier.padding(start = 12.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_close)) }
        }
    )
}
