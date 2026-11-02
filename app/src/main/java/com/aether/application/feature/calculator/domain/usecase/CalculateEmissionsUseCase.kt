package com.aether.application.feature.calculator.domain.usecase

import com.aether.application.feature.calculator.domain.model.ReductionForecast
import com.aether.application.feature.calculator.domain.model.ReductionForecastParams
import com.aether.application.feature.calculator.domain.repository.CalculatorRepository

class CalculateEmissionsUseCase(
    private val calculatorRepository: CalculatorRepository
) {
    suspend operator fun invoke(params: ReductionForecastParams): ReductionForecast =
        calculatorRepository.forecastReduction(params)
}
