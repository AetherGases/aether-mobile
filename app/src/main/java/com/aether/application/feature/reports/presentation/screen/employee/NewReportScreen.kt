package com.aether.application.feature.reports.presentation.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aether.core.ui.components.AetherTopBar
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
            .background(Color.White)
    ) {
        AetherTopBar(title = "Criar relatório", onBackClick = onBackClicked)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Text("Anexe sua planilha", style = titleLarge, color = textPrimaryLight)
            Spacer(Modifier.height(8.dp))
            Text(
                "Envie o arquivo Excel (.xlsx) com os dados do seu relatório.",
                style = bodyMediumMd,
                color = textTertiaryLight
            )

            Spacer(Modifier.height(48.dp))

            SelectFileRow(
                onClick = { filePickerLauncher.launch(spreadsheetMimeTypes) }
            )

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
                            Brush.linearGradient(listOf(Color(0xFF8A47FD), purple500))
                        } else {
                            Brush.linearGradient(listOf(textDisabledLight, textDisabledLight))
                        }
                    )
                    .clickable(enabled = isFileReady) { showNameDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Enviar",
                    style = titleMedium,
                    color = if (isFileReady) Color.White else Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }

    if (showNameDialog) {
        ReportNameDialog(
            reportName = reportName,
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
            .background(green500.copy(alpha = 0.10f))
            .border(1.dp, green500.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
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
            Text("Selecionar arquivo", style = titleSmall, color = textSecondaryLight)
            Text("Aceitamos arquivos .xlsx", style = bodyMediumMd, color = green500)
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = green500,
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
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(textDisabledLight.copy(alpha = 0.2f)),
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
                        val brush = Brush.linearGradient(listOf(purple300, purple500))
                        onDrawWithContent {
                            drawContent()
                            drawRect(brush, blendMode = BlendMode.SrcAtop)
                        }
                    }
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(fileName, style = bodyMedium, color = textPrimaryLight)
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(textDisabledLight.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(green500)
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        IconButton(onClick = onCancelClick, modifier = Modifier.size(24.dp)) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remover arquivo",
                tint = textTertiaryLight
            )
        }
    }
}

@Composable
private fun ReportNameDialog(
    reportName: String,
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
            color = Color.White,
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
                        "Nome do relatório",
                        style = titleSmall,
                        color = textPrimaryLight
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = textTertiaryLight
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = reportName,
                    onValueChange = onReportNameChanged,
                    placeholder = { Text("Ex: Relatório de emissões - Julho") },
                    singleLine = true,
                    shape = RoundedCornerShape(percent = 50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purple300,
                        unfocusedBorderColor = textDisabledLight,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                val canConfirm = reportName.isNotBlank()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (canConfirm) {
                                Brush.linearGradient(listOf(purple300, purple500))
                            } else {
                                Brush.linearGradient(listOf(textDisabledLight, textDisabledLight))
                            }
                        )
                        .clickable(enabled = canConfirm, onClick = onConfirm),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Criar relatório", style = titleMediumMD, color = Color.White)
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
            onReportNameChanged = {},
            onFileSelected = {},
            onRemoveFileClicked = {},
            onConfirmSubmit = {},
            onBackClicked = {}
        )
    }
}