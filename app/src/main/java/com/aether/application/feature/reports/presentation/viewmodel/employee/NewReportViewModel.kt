package com.aether.application.feature.reports.presentation.viewmodel.employee

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.feature.reports.domain.model.ConsolidationApproach
import com.aether.application.feature.reports.domain.model.ReportException
import com.aether.application.feature.reports.domain.model.isSpreadsheetFileName
import com.aether.application.feature.reports.domain.model.validateReportName
import com.aether.application.feature.reports.domain.repository.NewReportRepository
import com.aether.application.feature.reports.presentation.state.NewReportEvent
import com.aether.application.feature.reports.presentation.state.NewReportUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "NewReportViewModel"

// The file only counts as sent once Cloudinary answers, so the bar stops just short of full
private const val MAX_PROGRESS_WHILE_SENDING = 0.99f

class NewReportViewModel(
    private val newReportRepository: NewReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewReportUiState())
    val uiState: StateFlow<NewReportUiState> = _uiState.asStateFlow()

    private val _events = Channel<NewReportEvent>(Channel.BUFFERED)
    val events: Flow<NewReportEvent> = _events.receiveAsFlow()

    private var uploadJob: Job? = null
    private var uploadedFileUrl: String? = null

    fun onFileSelected(uri: Uri) {
        val fileName = newReportRepository.displayName(uri.toString())
        if (fileName == null || !isSpreadsheetFileName(fileName)) {
            _uiState.update { it.copy(errorMessage = "Selecione uma planilha no formato .xlsx.") }
            return
        }

        uploadJob?.cancel()
        uploadedFileUrl = null
        _uiState.update { it.copy(selectedFileName = fileName, uploadProgress = 0f, errorMessage = null) }

        uploadJob = viewModelScope.launch {
            val job = coroutineContext[Job]!!
            try {
                uploadedFileUrl = newReportRepository.uploadSpreadsheet(uri.toString(), fileName) { progress ->
                    // Callbacks come from the network thread and may arrive after a cancel
                    if (job.isActive) {
                        _uiState.update {
                            it.copy(uploadProgress = progress.coerceIn(0f, MAX_PROGRESS_WHILE_SENDING))
                        }
                    }
                }
                _uiState.update { it.copy(uploadProgress = 1f) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to upload spreadsheet", e)
                _uiState.update {
                    it.copy(
                        selectedFileName = null,
                        uploadProgress = null,
                        errorMessage = e.userMessage()
                    )
                }
            }
        }
    }

    fun onRemoveFileClicked() {
        uploadJob?.cancel()
        uploadedFileUrl = null
        _uiState.update { it.copy(selectedFileName = null, uploadProgress = null, errorMessage = null) }
    }

    fun onReportNameChanged(name: String) {
        _uiState.update {
            it.copy(
                reportName = name,
                reportNameError = if (name.isBlank()) null else validateReportName(name)
            )
        }
    }

    fun onApproachSelected(approach: ConsolidationApproach) {
        val state = _uiState.value
        val fileName = state.selectedFileName
        val fileUrl = uploadedFileUrl
        if (state.isSubmitting) return

        val nameError = validateReportName(state.reportName)
        if (nameError != null || fileName == null || fileUrl == null) {
            _uiState.update {
                it.copy(errorMessage = nameError ?: "Envie a planilha antes de criar o relatório.")
            }
            return
        }

        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val report = newReportRepository.createReport(
                    name = state.reportName.trim(),
                    approach = approach,
                    fileName = fileName,
                    fileUrl = fileUrl
                )
                _uiState.update { it.copy(isSubmitting = false) }
                _events.send(NewReportEvent.Created(report.name))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create report", e)
                _uiState.update { it.copy(isSubmitting = false, errorMessage = e.userMessage()) }
            }
        }
    }

    /** Both steps share this state, so the error of one must not follow the user to the other. */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun Exception.userMessage(): String =
        (this as? ReportException)?.message ?: "Algo deu errado. Tente novamente."
}
