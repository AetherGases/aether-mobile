package com.aether.application.feature.calculator.data.remote.dto

import com.aether.application.feature.calculator.domain.model.GasOption
import com.aether.application.feature.calculator.domain.model.ReductionForecast
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GasCatalogItemResponse(
    val id: Int,
    val name: String,
    val formula: String,
    @SerialName("is_green") val isGreen: Boolean,
    val gwp: Double,
) {
    fun toDomain() = GasOption(id = id, name = name, formula = formula, isGreen = isGreen)
}

@Serializable
data class EmissionDto(
    val gas: Int,
    @SerialName("volume_co2e") val volumeCo2e: Double,
    val scope: Int,
)

@Serializable
data class ReductionForecastRequest(
    val years: Int,
    @SerialName("current_emission") val currentEmission: EmissionDto,
    @SerialName("green_gas") val greenGas: Int,
)

@Serializable
data class ReductionForecastResponse(
    @SerialName("future_emission") val futureEmission: EmissionDto,
    @SerialName("reduction_co2e") val reductionCo2e: Double,
) {
    fun toDomain() = ReductionForecast(
        futureVolumeCo2e = futureEmission.volumeCo2e,
        reductionCo2e = reductionCo2e,
    )
}
