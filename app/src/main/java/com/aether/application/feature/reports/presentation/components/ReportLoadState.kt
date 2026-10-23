package com.aether.application.feature.reports.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aether.core.ui.theme.bodyMediumMd
import com.aether.core.ui.theme.titleMediumMD

/** Full-screen placeholder shown while a report loads or after it failed to load. */
@Composable
fun ReportLoadState(
    errorMessage: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (errorMessage == null) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            Text(
                text = errorMessage,
                style = bodyMediumMd,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            TextButton(onClick = onRetry) {
                Text(
                    text = "Tentar novamente",
                    style = titleMediumMD,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
