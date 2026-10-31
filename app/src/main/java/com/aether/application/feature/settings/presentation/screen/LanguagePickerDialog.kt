package com.aether.application.feature.settings.presentation.screen

import androidx.compose.foundation.layout.Column
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
import com.aether.application.core.utils.AppLanguage

@Composable
fun AppLanguage.label(): String = stringResource(
    when (this) {
        AppLanguage.SYSTEM -> R.string.settings_language_system
        AppLanguage.PT_BR -> R.string.settings_language_pt_br
        AppLanguage.EN -> R.string.settings_language_en
    }
)

@Composable
fun LanguagePickerDialog(
    selected: AppLanguage,
    onSelect: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_language)) },
        text = {
            Column(modifier = Modifier.selectableGroup()) {
                AppLanguage.entries.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = language == selected,
                                onClick = { onSelect(language) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = language == selected, onClick = null)
                        Text(language.label(), modifier = Modifier.padding(start = 12.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_close)) }
        }
    )
}
