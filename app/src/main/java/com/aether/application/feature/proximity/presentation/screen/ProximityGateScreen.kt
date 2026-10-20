package com.aether.application.feature.proximity.presentation.screen

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.R
import com.aether.application.feature.proximity.domain.ProximityStatus
import com.aether.application.feature.proximity.domain.WORKPLACE_RADIUS_METERS
import com.aether.core.ui.theme.AetherTheme
import com.aether.core.ui.theme.bodyMediumMd
import com.aether.core.ui.theme.purple500
import com.aether.core.ui.theme.titleLarge
import com.aether.core.ui.theme.titleMediumMD

/**
 * Covers the app while the first proximity check runs ([status] null)
 * or when the device is not allowed to use it.
 */
@Composable
fun ProximityGateScreen(
    status: ProximityStatus?,
    isChecking: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { onRetry() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            // Swallows touches so the app below can't be used
            .clickable(enabled = true, onClick = {}, indication = null, interactionSource = null),
        contentAlignment = Alignment.Center
    ) {
        if (status == null) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            return@Box
        }

        val radiusKm = WORKPLACE_RADIUS_METERS / 1000
        val (title, description) = when (status) {
            ProximityStatus.PermissionRequired ->
                "Permita o acesso à localização" to
                    "O Aether só pode ser usado nas proximidades da sua empresa. Precisamos da sua localização para confirmar isso."
            ProximityStatus.LocationDisabled ->
                "Ative a localização" to
                    "A localização do dispositivo está desligada. Ative-a para confirmarmos que você está na sua empresa."
            ProximityStatus.LocationUnavailable ->
                "Não foi possível obter sua localização" to
                    "Verifique o sinal de GPS ou a conexão e tente novamente."
            is ProximityStatus.TooFar ->
                "Você está longe da empresa" to
                    "O Aether só pode ser usado a até ${formatKm(radiusKm)} km da sua empresa. Você está a cerca de ${formatKm(status.distanceMeters / 1000f)} km."
            ProximityStatus.Allowed -> return@Box
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_factory_illustration),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(160.dp)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = title,
                style = titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = description,
                style = bodyMediumMd,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            when (status) {
                ProximityStatus.PermissionRequired -> {
                    GateButton(text = "Permitir localização", enabled = !isChecking) {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                    // Once denied for good the system no longer shows the prompt
                    TextButton(onClick = {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null)
                            )
                        )
                    }) {
                        Text("Abrir configurações", style = titleMediumMD, color = MaterialTheme.colorScheme.primary)
                    }
                }
                ProximityStatus.LocationDisabled -> {
                    GateButton(text = "Ativar localização", enabled = !isChecking) {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    TextButton(onClick = onRetry, enabled = !isChecking) {
                        Text("Tentar novamente", style = titleMediumMD, color = MaterialTheme.colorScheme.primary)
                    }
                }
                else -> GateButton(
                    text = if (isChecking) "Verificando..." else "Tentar novamente",
                    enabled = !isChecking,
                    onClick = onRetry
                )
            }
        }
    }
}

@Composable
private fun GateButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(100))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF8B5CF6), purple500).map { if (enabled) it else it.copy(alpha = 0.4f) }
                )
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = titleMediumMD, color = MaterialTheme.colorScheme.onPrimary)
    }
}

private fun formatKm(value: Float): String = "%.1f".format(value).replace('.', ',')

@Preview(showBackground = true)
@Composable
private fun ProximityGateTooFarPreview() {
    AetherTheme {
        ProximityGateScreen(
            status = ProximityStatus.TooFar(distanceMeters = 12_400),
            isChecking = false,
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProximityGatePermissionPreview() {
    AetherTheme {
        ProximityGateScreen(
            status = ProximityStatus.PermissionRequired,
            isChecking = false,
            onRetry = {}
        )
    }
}
