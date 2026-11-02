package com.aether.application.feature.calculator.domain.repository

import com.aether.application.feature.calculator.domain.model.GasOption
import com.aether.application.feature.calculator.domain.model.ReductionForecast
import com.aether.application.feature.calculator.domain.model.ReductionForecastParams

interface CalculatorRepository {
    suspend fun getGases(): List<GasOption>
    suspend fun forecastReduction(params: ReductionForecastParams): ReductionForecast
}
