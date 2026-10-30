package com.aether.application.feature.calculator.domain.model

data class GasOption(
    val id: Int,
    val name: String,
    val formula: String,
    val isGreen: Boolean,
) {
    val label: String get() = "$name ($formula)"
}

data class ReductionForecast(
    val futureVolumeCo2e: Double,
    val reductionCo2e: Double,
)

data class ReductionForecastParams(
    val currentGasId: Int,
    val greenGasId: Int,
    val annualVolumeCo2e: Double,
    val scope: Int,
    val years: Int,
)
