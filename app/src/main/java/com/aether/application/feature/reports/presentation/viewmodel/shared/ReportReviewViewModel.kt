package com.aether.application.feature.reports.presentation.viewmodel.shared

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.auth.domain.model.AppPermission
import com.aether.application.feature.home.domain.model.InventoryStatus
import com.aether.application.feature.reports.domain.model.ReportDetails
import com.aether.application.feature.reports.domain.repository.ReportsRepository
import com.aether.application.feature.reports.presentation.screen.shared.GasEmissionUi
import com.aether.application.feature.reports.presentation.screen.shared.ReportReviewUi
import com.aether.application.feature.reports.presentation.screen.shared.ScopeGasesUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val TAG = "ReportReviewViewModel"

private val analyzableStatuses = setOf(
    InventoryStatus.Input.UNDER_REVIEW,
    InventoryStatus.Input.APPROVED,
    InventoryStatus.Input.REJECTED
)

data class ReportReviewUiState(
    val report: ReportReviewUi? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class ReportReviewViewModel(
    private val reportId: Long,
    private val reportsRepository: ReportsRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportReviewUiState())
    val uiState: StateFlow<ReportReviewUiState> = _uiState.asStateFlow()

    private val locale = Locale.forLanguageTag("pt-BR")
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", locale)
    private val numberFormat = NumberFormat.getNumberInstance(locale).apply {
        maximumFractionDigits = 1
    }

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val canReview = sessionManager.hasPermission(AppPermission.INVENTORY_ANALYSIS)
                val report = reportsRepository.getReport(reportId, asReviewer = canReview)
                _uiState.update { it.copy(report = report.toUi(canReview), isLoading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load report $reportId", e)
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = "Não foi possível carregar o relatório."
                ) }
            }
        }
    }

    private fun ReportDetails.toUi(canReview: Boolean) = ReportReviewUi(
        name = name,
        sector = sector,
        unitName = unitName ?: "—",
        // pt-BR month abbreviations come with a trailing dot ("jul.")
        submittedAt = createdAt.format(dateFormatter).replace(".", ""),
        description = description,
        reviewerName = reviewer?.name,
        reviewerAvatarUrl = reviewer?.avatarUrl,
        status = status,
        validationMessage = validationMessage,
        scopes = emissionsByScope.map { scope ->
            ScopeGasesUi(
                scope = scope.scope,
                gases = scope.gases.map { gas ->
                    GasEmissionUi(
                        name = gas.name,
                        formula = gas.formula,
                        quantity = numberFormat.format(gas.quantityCo2e)
                    )
                }
            )
        },
        canProceedToAnalysis = canReview && status in analyzableStatuses
    )
}
