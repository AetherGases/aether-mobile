package com.aether.application.feature.reports.domain.model

import com.aether.application.feature.home.domain.model.InventoryStatus
import java.time.LocalDateTime

data class ReportDetails(
    val id: Long,
    val name: String,
    val sector: String?,
    val unitName: String?,
    val description: String?,
    val createdAt: LocalDateTime,
    val status: InventoryStatus,
    /** Null while the report has not been reviewed yet. */
    val reviewer: ReportReviewer?,
    /** Approval comment or rejection reason left by the reviewer. */
    val validationMessage: String?,
    val emissionsByScope: List<ScopeEmissions>,
)

data class ReportReviewer(
    val name: String,
    val avatarUrl: String?,
)

data class ScopeEmissions(
    val scope: String,
    val gases: List<GasEmission>,
)

/** [quantityCo2e] in tCO2e/month. */
data class GasEmission(
    val name: String,
    val formula: String,
    val quantityCo2e: Double,
)

/** Emission values in tCO2e/month, money values in BRL. */
data class ReportAnalysis(
    val totalEmissions: Double,
    /** Change against the previous period; positive means emissions went up. */
    val changePercent: Double,
    val scopes: List<ScopeShare>,
    val priorityReplacement: Replacement?,
    val secondaryReplacement: Replacement?,
    val monthlySavings: Long,
    val initialInvestment: Long,
)

data class ScopeShare(
    val name: String,
    val percent: Int,
)

data class Replacement(
    val reduction: Double,
    val percent: Int,
    val technology: String,
    val scope: String,
)

/** Textual plan produced by Aeko for an inventory. */
data class ImprovementPlan(
    val definedProblem: String,
    val solvingMethod: String,
    val reasoning: String,
)
