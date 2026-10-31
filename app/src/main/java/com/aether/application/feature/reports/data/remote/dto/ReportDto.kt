package com.aether.application.feature.reports.data.remote.dto

import com.aether.application.core.serialization.LocalDateSerializer
import com.aether.application.core.serialization.LocalDateTimeSerializer
import com.aether.application.feature.home.domain.model.inventoryStatusOf
import com.aether.application.feature.reports.domain.model.GasEmission
import com.aether.application.feature.reports.domain.model.ImprovementPlan
import com.aether.application.feature.reports.domain.model.ReportAnalysis
import com.aether.application.feature.reports.domain.model.ReportDetails
import com.aether.application.feature.reports.domain.model.RecommendedGas
import com.aether.application.feature.reports.domain.model.ReportReviewer
import com.aether.application.feature.reports.domain.model.ScopeEmissions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

/** Response of ms-inventory `GET /api/inventories/mine/{id}` and `/reviewed/{id}`; null fields are omitted. */
@Serializable
data class GetReportResponse(
    val id: Long,
    val name: String,
    val description: String? = null,
    val consolidationApproach: String? = null,
    @Serializable(with = LocalDateSerializer::class)
    val inventoryPeriodStart: LocalDate? = null,
    @Serializable(with = LocalDateSerializer::class)
    val inventoryPeriodEnd: LocalDate? = null,
    val type: String,
    val status: String,
    val validationMessage: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime? = null,
    val owner: EmployeeDto? = null,
    val validator: EmployeeDto? = null,
    val storageFile: StorageFileDto? = null,
    // TODO: not sent by ms-inventory yet, the shape follows its nested records but isn't agreed on
    val emissions: List<EmissionDto> = emptyList(),
) {
    fun toDomain() = ReportDetails(
        id = id,
        name = name,
        // TODO: ms-inventory sends neither the sector nor the unit name
        sector = null,
        unitName = null,
        description = description,
        createdAt = createdAt,
        status = inventoryStatusOf(status, type),
        reviewer = validator?.let { ReportReviewer(name = it.name, avatarUrl = it.imageUrl) },
        validationMessage = validationMessage,
        emissionsByScope = emissions
            .groupBy { it.scope.name }
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
data class StorageFileDto(
    val id: Long,
    val fileName: String,
    val path: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime? = null,
)

@Serializable
data class EmissionDto(
    val quantityCo2e: Double,
    val scope: ScopeDto,
    val gas: GasDto,
)

@Serializable
data class ScopeDto(
    val id: Long,
    val name: String,
)

@Serializable
data class GasDto(
    val id: Long,
    val name: String,
    val formula: String,
)

/** Response of the (still guessed) ms-inventory analysis route; scope shares are derived from the report emissions. */
@Serializable
data class GetReportAnalysisResponse(
    val totalEmissions: Double,
    val changePercent: Double,
    val monthlySavings: Long,
) {
    fun toDomain() = ReportAnalysis(
        totalEmissions = totalEmissions,
        changePercent = changePercent,
        monthlySavings = monthlySavings
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
    // TODO: field names of the recommended gases are assumed, not agreed on with the hub yet
    @SerialName("recommended_gases")
    val recommendedGases: List<RecommendedGasDto> = emptyList(),
) {
    fun toDomain() = ImprovementPlan(
        definedProblem = definedProblem,
        solvingMethod = solvingMethod,
        reasoning = reasoning,
        // The lower the index, the higher the priority
        recommendedGases = recommendedGases.sortedBy { it.index }.map { it.toDomain() }
    )
}

@Serializable
data class RecommendedGasDto(
    val index: Int,
    /** 1, 2 or 3. */
    val scope: Int,
    val emission: Int,
    @SerialName("reduction_percentage")
    val reductionPercentage: Int,
    val name: String,
) {
    fun toDomain() = RecommendedGas(
        scope = scope,
        emission = emission,
        reductionPercentage = reductionPercentage,
        name = name
    )
}

/** Body of ms-inventory `PATCH /api/inventories/pending/{id}/approve|reject`. */
@Serializable
data class ReviewReportRequest(
    val message: String,
)
