package com.aether.application.feature.home.domain.model

/** Values in tCO2e, except [reductionPercentage]. */
data class EnterpriseInfo(
    val totalReductions: Int,
    val totalEmissions: Int,
)

data class InventoryCount(
    val submittedCount: Int,
    val reviewedCount: Int,
)

data class UnitInfo(
    val emissions: Int,
    val reductionPercentage: Int,
)


/** Progress towards the Selo Paraná. */
data class SealProgress(
    val criteriaMet: Int,
    val criteriaTotal: Int,
) {
    val percent: Int
        get() = if (criteriaTotal > 0) criteriaMet * 100 / criteriaTotal else 0
}
