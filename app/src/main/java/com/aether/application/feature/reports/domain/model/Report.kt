package com.aether.application.feature.reports.domain.model

import com.aether.application.feature.home.domain.model.InventoryStatus
import java.time.LocalDateTime

data class ReportDetails(
    val id: Long,
    val name: String,
    val sector: String?,
    val unitName: String?,
    val description: String?,
    val ownerId: Long?,
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
    val monthlySavings: Long,
)

/** Textual plan produced by Aeko for an inventory. */
data class ImprovementPlan(
    val definedProblem: String,
    val solvingMethod: String,
    val reasoning: String,
    /** Gases to replace, highest priority first. */
    val recommendedGases: List<RecommendedGas>,
)

data class RecommendedGas(
    /** 1, 2 or 3. */
    val scope: Int,
    /** In tCO2e/month. */
    val emission: Int,
    /** 1-100. */
    val reductionPercentage: Int,
    val name: String,
)
