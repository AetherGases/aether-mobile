package com.aether.application.feature.qa.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.core.ui.theme.AetherTheme
import com.aether.core.ui.theme.lightRed
import com.aether.core.ui.theme.purple300
import com.aether.core.ui.theme.textDisabledLight
import com.aether.core.ui.theme.textPrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerConfigScreen(
    domain: String,
    onDomainChange: (String) -> Unit,
    savedDomains: List<String>,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth().padding(24.dp)) {
        Text(
            text = "Servidor de QA",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(24.dp))

        ExposedDropdownMenuBox(
            expanded = expanded && savedDomains.isNotEmpty(),
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = domain,
                onValueChange = onDomainChange,
                label = { Text("Domínio") },
                placeholder = { Text("http://192.168.0.10:8080/") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textPrimaryLight,
                    unfocusedTextColor = textPrimaryLight,
                    focusedBorderColor = purple300,
                    unfocusedBorderColor = textDisabledLight,
                    disabledBorderColor = textDisabledLight,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorTextColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error,
                    errorCursorColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryEditable)
            )

            ExposedDropdownMenu(
                expanded = expanded && savedDomains.isNotEmpty(),
                onDismissRequest = { expanded = false }
            ) {
                savedDomains.forEach { savedDomain ->
                    DropdownMenuItem(
                        text = { Text(savedDomain) },
                        onClick = {
                            onDomainChange(savedDomain)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar e usar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServerConfigScreenPreview() {
    AetherTheme {
        ServerConfigScreen(
            domain = "http://192.168.0.10:8080/",
            onDomainChange = {},
            savedDomains = listOf("http://192.168.0.10:8080/", "http://10.0.2.2:8080/"),
            onSaveClick = {}
        )
    }
}
