package com.aether.application.feature.calculator.presentation.state

import com.aether.application.core.utils.UiText
import com.aether.application.feature.calculator.domain.model.GasOption
import com.aether.application.feature.calculator.presentation.screen.CalculatorResultUi

enum class CalculatorStep { EMISSION, GREEN_GAS, DEADLINE, RESULT }

data class CalculatorUiState(
    val step: CalculatorStep = CalculatorStep.EMISSION,
    val gases: List<GasOption> = emptyList(),
    val pollutingGas: String = "",
    val monthlyVolume: String = "",
    val scope: String = "",
    val greenGas: String = "",
    val deadline: String = "",
    val isLoading: Boolean = false,
    val errorMessage: UiText? = null,
    val result: CalculatorResultUi? = null,
) {
    val pollutingGasOptions: List<String> get() = gases.filter { !it.isGreen }.map { it.label }
    val greenGasOptions: List<String> get() = gases.filter { it.isGreen }.map { it.label }
}
