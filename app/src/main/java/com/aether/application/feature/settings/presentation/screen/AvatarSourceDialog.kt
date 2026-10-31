package com.aether.application.feature.settings.presentation.screen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.aether.application.R
import com.aether.core.ui.theme.bodyLargeMedium
import com.aether.core.ui.theme.titleLarge
import com.aether.core.ui.theme.titleMediumMD
import java.io.File

private const val AVATAR_CACHE_DIR = "avatars"

/** Lets the user take a new photo or pick one from the gallery. */
@Composable
fun AvatarSourceDialog(
    onDismiss: () -> Unit,
    onPicked: (Uri) -> Unit
) {
    val context = LocalContext.current
    // Survives the activity being recreated while the camera is open
    var cameraUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let(onPicked)
        onDismiss()
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { saved ->
        val uri = cameraUri
        if (saved && uri != null) onPicked(uri)
        onDismiss()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_profile_photo), style = titleLarge) },
        text = {
            Column {
                AvatarSourceOption(
                    icon = Icons.Outlined.PhotoCamera,
                    label = stringResource(R.string.settings_avatar_take_photo),
                    onClick = {
                        val uri = newCameraUri(context)
                        cameraUri = uri
                        cameraLauncher.launch(uri)
                    }
                )
                AvatarSourceOption(
                    icon = Icons.Outlined.PhotoLibrary,
                    label = stringResource(R.string.settings_avatar_choose_gallery),
                    onClick = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.settings_cancel), style = titleMediumMD, color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

@Composable
private fun AvatarSourceOption(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(label, style = bodyLargeMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

/** File in the app cache, shared with the camera app through the FileProvider. */
private fun newCameraUri(context: Context): Uri {
    val directory = File(context.cacheDir, AVATAR_CACHE_DIR).apply { mkdirs() }
    val file = File(directory, "avatar_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
