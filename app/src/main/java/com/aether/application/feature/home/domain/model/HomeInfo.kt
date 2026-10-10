package com.aether.application.feature.home.domain.model

/** Values in tCO2e, except [reductionPercentage]. */
data class EnterpriseInfo(
    val totalReductions: Int,
    val totalEmissions: Int,
)

data class UnitInfo(
    val emissions: Int,
    val reductionPercentage: Int,
)
