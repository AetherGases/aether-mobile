package com.aether.application.feature.reports.presentation.viewmodel.manager

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.R
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.core.utils.UiText
import com.aether.application.core.utils.uiText
import com.aether.application.feature.auth.domain.model.AppPermission
import com.aether.application.feature.home.domain.model.InventoryStatus
import com.aether.application.feature.reports.domain.model.ImprovementPlan
import com.aether.application.feature.reports.domain.model.Replacement
import com.aether.application.feature.reports.domain.model.ReportAnalysis
import com.aether.application.feature.reports.domain.repository.ReportsRepository
import com.aether.application.feature.reports.presentation.screen.manager.AetherAnalysisUi
import com.aether.application.feature.reports.presentation.screen.manager.ImprovementPlanUi
import com.aether.application.feature.reports.presentation.screen.manager.ReplacementUi
import com.aether.application.feature.reports.presentation.screen.manager.ScopeUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val TAG = "ReportAnalysisViewModel"

private val decidedStatuses = setOf(
    InventoryStatus.Input.APPROVED,
    InventoryStatus.Input.REJECTED
)

data class ReportAnalysisUiState(
    val analysis: AetherAnalysisUi? = null,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    /** Shows the approve/reject buttons: the user can review and the report is still open. */
    val canDecide: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitErrorMessage: UiText? = null,
    /** True when approved, false when rejected, null while not reviewed. */
    val decision: Boolean? = null,
)

class ReportAnalysisViewModel(
    private val reportId: Long,
    private val reportsRepository: ReportsRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportAnalysisUiState())
    val uiState: StateFlow<ReportAnalysisUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Without the permission the analysis still shows, only the decision is hidden
            val canReview = sessionManager.hasPermission(AppPermission.INVENTORY_ANALYSIS)

            try {
                val (analysis, status) = coroutineScope {
                    val plan = async { loadImprovementPlan() }
                    val analysis = async { reportsRepository.getReportAnalysis(reportId) }
                    val report = async { reportsRepository.getReport(reportId, asReviewer = canReview) }
                    analysis.await().toUi(plan.await()) to report.await().status
                }
                _uiState.update { it.copy(
                    analysis = analysis,
                    canDecide = canReview && status !in decidedStatuses,
                    isLoading = false
                ) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load analysis of report $reportId", e)
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = uiText(R.string.reports_analysis_error_load)
                ) }
            }
        }
    }

    // The plan text comes from Aeko; when it is unavailable the rest of the analysis still shows
    private suspend fun loadImprovementPlan(): ImprovementPlan? =
        try {
            reportsRepository.getImprovementPlan(reportId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load improvement plan of report $reportId", e)
            null
        }

    fun onDecisionSubmit(approved: Boolean, comment: String) {
        if (_uiState.value.isSubmitting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitErrorMessage = null) }
            try {
                if (approved) {
                    reportsRepository.approveReport(reportId, comment)
                } else {
                    reportsRepository.rejectReport(reportId, comment)
                }
                _uiState.update { it.copy(isSubmitting = false, decision = approved) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to submit review of report $reportId", e)
                _uiState.update { it.copy(
                    isSubmitting = false,
                    submitErrorMessage = uiText(R.string.reports_analysis_error_submit)
                ) }
            }
        }
    }

    private fun ReportAnalysis.toUi(plan: ImprovementPlan?) = AetherAnalysisUi(
        totalEmissions = totalEmissions.roundToInt(),
        changePercent = changePercent.roundToInt(),
        isEmissionIncrease = changePercent > 0,
        scopes = scopes.map { ScopeUi(name = it.name, percent = it.percent) },
        improvements = listOfNotNull(
            plan?.let {
                ImprovementPlanUi(
                    id = "$reportId",
                    definedProblem = it.definedProblem,
                    method = it.solvingMethod,
                    reasoning = it.reasoning
                )
            }
        ),
        priority = priorityReplacement?.toUi(R.string.reports_analysis_priority_replacement),
        secondary = secondaryReplacement?.toUi(R.string.reports_analysis_secondary_replacement),
        monthlySavings = monthlySavings,
        initialInvestment = initialInvestment
    )

    private fun Replacement.toUi(@StringRes titleRes: Int) = ReplacementUi(
        titleRes = titleRes,
        reduction = reduction.roundToInt(),
        percent = percent,
        technology = technology,
        scope = scope
    )
}
