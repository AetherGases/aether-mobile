package com.aether.application.feature.home.presentation.screen

import androidx.compose.foundation.background
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
import com.aether.application.core.ui.theme.bodyMediumMd
import com.aether.application.core.ui.theme.titleLarge
import com.aether.application.core.ui.theme.titleMediumMD

/** Shown until the profile, the permissions and the home sections are loaded. */
@Composable
fun HomeLoadingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

/** The profile loaded fine but the user has no permission to use anything yet. */
@Composable
fun HomeNoPermissionsScreen(
    onRetryClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Você ainda não tem permissões",
            style = titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Sua conta não tem nenhuma permissão no momento. Fale com os responsáveis da sua empresa para liberar o seu acesso.",
            style = bodyMediumMd,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp)
        )
        TextButton(onClick = onRetryClick, modifier = Modifier.padding(top = 16.dp)) {
            Text("Verificar novamente", style = titleMediumMD, color = MaterialTheme.colorScheme.primary)
        }
        TextButton(onClick = onLogoutClick) {
            Text("Sair", style = titleMediumMD, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
