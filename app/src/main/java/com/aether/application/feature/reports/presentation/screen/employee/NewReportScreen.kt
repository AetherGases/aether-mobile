package com.aether.application.feature.reports.presentation.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aether.application.core.ui.components.FormErrorText
import com.aether.application.core.ui.components.AetherTopBar
import com.aether.application.core.utils.UiText
import com.aether.core.ui.theme.*
import com.aether.application.R

private val spreadsheetMimeTypes = arrayOf(
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    "application/vnd.ms-excel"
)

@Composable
fun CreateReportUploadScreen(
    selectedFileName: String?,
    uploadProgress: Float?,
    reportName: String,
    reportNameError: UiText?,
    errorMessage: UiText?,
    onReportNameChanged: (String) -> Unit,
    onFileSelected: (Uri) -> Unit,
    onRemoveFileClicked: () -> Unit,
    onConfirmSubmit: () -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showNameDialog by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {uri: Uri? ->
        uri?.let(onFileSelected)

    }

    val isFileReady = selectedFileName != null && (uploadProgress ?: 0f) >= 1f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AetherTopBar(title = stringResource(R.string.reports_new_title), onBackClick = onBackClicked)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Text(stringResource(R.string.reports_new_attach_title), style = titleLarge, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.reports_new_attach_description),
                style = bodyMediumMd,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(Modifier.height(48.dp))

            SelectFileRow(
                onClick = { filePickerLauncher.launch(spreadsheetMimeTypes) }
            )

            if (errorMessage != null) {
                Spacer(Modifier.height(12.dp))
                FormErrorText(errorMessage.asString())
            }

            Spacer(Modifier.weight(1f))

            if (selectedFileName != null) {
                FileUploadProgressRow(
                    fileName = selectedFileName,
                    progress = uploadProgress ?: 0f,
                    onCancelClick = onRemoveFileClicked
                )
                Spacer(Modifier.height(16.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isFileReady) {
                            Brush.linearGradient(
                                listOf(MaterialTheme.colorScheme.inversePrimary, MaterialTheme.colorScheme.primary)
                            )
                        } else {
                            SolidColor(MaterialTheme.colorScheme.outlineVariant)
                        }
                    )
                    .clickable(enabled = isFileReady) { showNameDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.reports_new_submit),
                    style = titleMedium,
                    color = if (isFileReady) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }

    if (showNameDialog) {
        ReportNameDialog(
            reportName = reportName,
            reportNameError = reportNameError,
            onReportNameChanged = onReportNameChanged,
            onConfirm = {
                showNameDialog = false
                onConfirmSubmit()
            },
            onDismiss = { showNameDialog = false }
        )
    }
}

@Composable
private fun SelectFileRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f))
            .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_spreadsheet),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.reports_new_select_file), style = titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(stringResource(R.string.reports_new_accepted_files), style = bodyMediumMd, color = MaterialTheme.colorScheme.secondary)
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun FileUploadProgressRow(
    fileName: String,
    progress: Float,
    onCancelClick: () -> Unit
) {
    val iconGradientStart = MaterialTheme.colorScheme.inversePrimary
    val iconGradientEnd = MaterialTheme.colorScheme.primary
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_spreadsheet),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(28.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        val brush = Brush.linearGradient(listOf(iconGradientStart, iconGradientEnd))
                        onDrawWithContent {
                            drawContent()
                            drawRect(brush, blendMode = BlendMode.SrcAtop)
                        }
                    }
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(fileName, style = bodyMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.secondary)
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        IconButton(onClick = onCancelClick, modifier = Modifier.size(24.dp)) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.reports_new_remove_file),
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun ReportNameDialog(
    reportName: String,
    reportNameError: UiText?,
    onReportNameChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.reports_new_name_title),
                        style = titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.common_close),
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = reportName,
                    onValueChange = onReportNameChanged,
                    placeholder = { Text(stringResource(R.string.reports_new_name_placeholder)) },
                    isError = reportNameError != null,
                    supportingText = reportNameError?.let { error -> { Text(error.asString()) } },
                    singleLine = true,
                    shape = RoundedCornerShape(percent = 50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                val canConfirm = reportName.isNotBlank() && reportNameError == null

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (canConfirm) {
                                Brush.linearGradient(
                                    listOf(MaterialTheme.colorScheme.inversePrimary, MaterialTheme.colorScheme.primary)
                                )
                            } else {
                                SolidColor(MaterialTheme.colorScheme.outlineVariant)
                            }
                        )
                        .clickable(enabled = canConfirm, onClick = onConfirm),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.reports_new_title),
                        style = titleMediumMD,
                        color = if (canConfirm) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CreateReportUploadScreenEmptyPreview() {
    AetherTheme {
        CreateReportUploadScreen(
            selectedFileName = null,
            uploadProgress = null,
            reportName = "",
            reportNameError = null,
            errorMessage = null,
            onReportNameChanged = {},
            onFileSelected = {},
            onRemoveFileClicked = {},
            onConfirmSubmit = {},
            onBackClicked = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CreateReportUploadScreenUploadingPreview() {
    AetherTheme {
        CreateReportUploadScreen(
            selectedFileName = "arquivoblablabla.xlsx",
            uploadProgress = 0.6f,
            reportName = "",
            reportNameError = null,
            errorMessage = null,
            onReportNameChanged = {},
            onFileSelected = {},
            onRemoveFileClicked = {},
            onConfirmSubmit = {},
            onBackClicked = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CreateReportUploadScreenReadyPreview() {
    AetherTheme {
        CreateReportUploadScreen(
            selectedFileName = "arquivoblablabla.xlsx",
            uploadProgress = 1f,
            reportName = "",
            reportNameError = null,
            errorMessage = null,
            onReportNameChanged = {},
            onFileSelected = {},
            onRemoveFileClicked = {},
            onConfirmSubmit = {},
            onBackClicked = {}
        )
    }
}