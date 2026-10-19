package com.aether.application.feature.reports.data.remote.dto

import com.aether.application.core.serialization.LocalDateTimeSerializer
import com.aether.application.feature.home.domain.model.InventoryStatus
import com.aether.application.feature.home.domain.model.InventoryStatusSerializer
import com.aether.application.feature.reports.domain.model.GasEmission
import com.aether.application.feature.reports.domain.model.ImprovementPlan
import com.aether.application.feature.reports.domain.model.Replacement
import com.aether.application.feature.reports.domain.model.ReportAnalysis
import com.aether.application.feature.reports.domain.model.ReportDetails
import com.aether.application.feature.reports.domain.model.ReportReviewer
import com.aether.application.feature.reports.domain.model.ScopeEmissions
import com.aether.application.feature.reports.domain.model.ScopeShare
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class GetReportResponse(
    val id: Long,
    val name: String,
    val sector: String? = null,
    val unitName: String,
    val description: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = InventoryStatusSerializer::class)
    val status: InventoryStatus,
    val validator: EmployeeDto? = null,
    val validationMessage: String? = null,
    val emissions: List<EmissionDto> = emptyList(),
) {
    fun toDomain() = ReportDetails(
        id = id,
        name = name,
        sector = sector,
        unitName = unitName,
        description = description,
        createdAt = createdAt,
        status = status,
        reviewer = validator?.let { ReportReviewer(name = it.name, avatarUrl = it.imageUrl) },
        validationMessage = validationMessage,
        emissionsByScope = emissions
            .groupBy { it.scope }
            .map { (scope, rows) ->
                ScopeEmissions(
                    scope = scope,
                    gases = rows.map {
                        GasEmission(
                            name = it.gas.name,
                            formula = it.gas.formula,
                            quantityCo2e = it.quantityCo2e
                        )
                    }
                )
            }
    )
}

@Serializable
data class EmployeeDto(
    val id: Long,
    val name: String,
    val imageUrl: String? = null,
)

@Serializable
data class EmissionDto(
    val scope: String,
    val gas: GasDto,
    val quantityCo2e: Double,
)

@Serializable
data class GasDto(
    val name: String,
    val formula: String,
)

@Serializable
data class GetReportAnalysisResponse(
    val totalEmissions: Double,
    val changePercent: Double,
    val scopes: List<ScopeShareDto>,
    val priorityReplacement: ReplacementDto? = null,
    val secondaryReplacement: ReplacementDto? = null,
    val monthlySavings: Long,
    val initialInvestment: Long,
) {
    fun toDomain() = ReportAnalysis(
        totalEmissions = totalEmissions,
        changePercent = changePercent,
        scopes = scopes.map { ScopeShare(name = it.name, percent = it.percent) },
        priorityReplacement = priorityReplacement?.toDomain(),
        secondaryReplacement = secondaryReplacement?.toDomain(),
        monthlySavings = monthlySavings,
        initialInvestment = initialInvestment
    )
}

@Serializable
data class ScopeShareDto(
    val name: String,
    val percent: Int,
)

@Serializable
data class ReplacementDto(
    val reduction: Double,
    val percent: Int,
    val technology: String,
    val scope: String,
) {
    fun toDomain() = Replacement(
        reduction = reduction,
        percent = percent,
        technology = technology,
        scope = scope
    )
}

/** Response of ms-aeko-hub `GET /ai/report/{id_external_inventory}`. */
@Serializable
data class GetImprovementPlanResponse(
    @SerialName("defined_problem")
    val definedProblem: String,
    @SerialName("solving_method")
    val solvingMethod: String,
    val reasoning: String,
) {
    fun toDomain() = ImprovementPlan(
        definedProblem = definedProblem,
        solvingMethod = solvingMethod,
        reasoning = reasoning
    )
}

/** Body of ms-inventory `PATCH /api/inventories/pending/{id}/approve|reject`. */
@Serializable
data class ReviewReportRequest(
    val message: String,
)
