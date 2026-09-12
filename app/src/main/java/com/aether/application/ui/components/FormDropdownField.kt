package com.aether.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import com.aether.application.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDropdownField(
    label: String,
    placeholder: String,
    value: String,
    options: List<String>,
    onValueSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(label, style = bodyMediumMd, color = textPrimaryLight)
            Spacer(Modifier.height(6.dp))
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {expanded = it}
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                placeholder = {Text(placeholder)},
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_dropdown),
                        contentDescription = "retorno"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = purple300,
                    unfocusedBorderColor = textDisabledLight
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {expanded = false}
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormDropdownFieldPreview() {
    AetherTheme {
        FormDropdownField(
            label = "Tipo de gás poluente*",
            placeholder = "Selecione o gás atual",
            value = "",
            options = listOf("CO₂", "N₂O", "CH₄", "SF₆"),
            onValueSelected = {}
        )
    }
}