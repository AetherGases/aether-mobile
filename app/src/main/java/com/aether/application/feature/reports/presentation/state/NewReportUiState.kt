package com.aether.application.feature.reports.presentation.state

import com.aether.application.core.utils.UiText


data class NewReportUiState(
    val selectedFileName: String? = null,
    /** 0 to 1 while the file is being sent, null when no file was picked. */
    val uploadProgress: Float? = null,
    val reportName: String = "",
    val reportNameError: UiText? = null,
    val isSubmitting: Boolean = false,
    val errorMessage: UiText? = null,
)

sealed interface NewReportEvent {
    data class Created(val name: String) : NewReportEvent
}
