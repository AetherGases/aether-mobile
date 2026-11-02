package com.aether.application.feature.calculator.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.core.utils.userMessage
import com.aether.application.feature.calculator.domain.model.ReductionForecast
import com.aether.application.feature.calculator.domain.model.ReductionForecastParams
import com.aether.application.feature.calculator.domain.repository.CalculatorRepository
import com.aether.application.feature.calculator.domain.usecase.CalculateEmissionsUseCase
import com.aether.application.feature.calculator.domain.usecase.ValidateInputUseCase
import com.aether.application.feature.calculator.presentation.screen.CalculatorResultUi
import com.aether.application.feature.calculator.presentation.state.CalculatorStep
import com.aether.application.feature.calculator.presentation.state.CalculatorUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

private const val TAG = "CalculatorViewModel"
private const val MONTHS_PER_YEAR = 12
private const val AXIS_TICK_COUNT = 6

class CalculatorScreenViewModel(
    private val calculatorRepository: CalculatorRepository,
    private val calculateEmissions: CalculateEmissionsUseCase,
    private val validateInput: ValidateInputUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        loadGases()
    }

    fun loadGases() {
        viewModelScope.launch {
            try {
                val gases = calculatorRepository.getGases()
                _uiState.update { it.copy(gases = gases) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load gases", e)
                _uiState.update { it.copy(errorMessage = e.userMessage()) }
            }
        }
    }

    // The error stays on screen until the user changes something
    fun onPollutingGasChanged(value: String) =
        _uiState.update { it.copy(pollutingGas = value, errorMessage = null) }
    fun onMonthlyVolumeChanged(value: String) =
        _uiState.update { it.copy(monthlyVolume = value, errorMessage = null) }
    fun onScopeChanged(value: String) = _uiState.update { it.copy(scope = value, errorMessage = null) }
    fun onGreenGasChanged(value: String) =
        _uiState.update { it.copy(greenGas = value, errorMessage = null) }
    fun onDeadlineChanged(value: String) =
        _uiState.update { it.copy(deadline = value, errorMessage = null) }

    fun onNextClicked() {
        val state = _uiState.value
        when (state.step) {
            CalculatorStep.EMISSION -> {
                val error = when {
                    state.pollutingGas.isBlank() -> "Selecione o gás poluente."
                    validateInput.parseVolume(state.monthlyVolume) == null ->
                        "Informe um volume de emissão válido."
                    validateInput.parseScope(state.scope) == null -> "Selecione o escopo."
                    else -> null
                }
                goTo(CalculatorStep.GREEN_GAS, error)
            }
            CalculatorStep.GREEN_GAS ->
                goTo(
                    CalculatorStep.DEADLINE,
                    if (state.greenGas.isBlank()) "Selecione o gás verde." else null
                )
            else -> Unit
        }
    }

    fun onBackClicked(): Boolean {
        val previous = when (_uiState.value.step) {
            CalculatorStep.EMISSION -> return false
            CalculatorStep.GREEN_GAS -> CalculatorStep.EMISSION
            CalculatorStep.DEADLINE -> CalculatorStep.GREEN_GAS
            CalculatorStep.RESULT -> CalculatorStep.DEADLINE
        }
        _uiState.update { it.copy(step = previous, errorMessage = null) }
        return true
    }

    fun onCalculateClicked() {
        val state = _uiState.value
        if (state.isLoading) return

        val years = validateInput.parseYears(state.deadline)
        val monthlyVolume = validateInput.parseVolume(state.monthlyVolume)
        val scope = validateInput.parseScope(state.scope)
        val currentGas = state.gases.firstOrNull { !it.isGreen && it.label == state.pollutingGas }
        val greenGas = state.gases.firstOrNull { it.isGreen && it.label == state.greenGas }

        if (years == null) {
            _uiState.update { it.copy(errorMessage = "Informe um prazo de pelo menos 1 ano.") }
            return
        }
        if (monthlyVolume == null || scope == null || currentGas == null || greenGas == null) {
            _uiState.update { it.copy(errorMessage = "Revise os dados informados.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val forecast = calculateEmissions(
                    ReductionForecastParams(
                        currentGasId = currentGas.id,
                        greenGasId = greenGas.id,
                        // The form asks per month, the backend forecasts per year
                        annualVolumeCo2e = monthlyVolume * MONTHS_PER_YEAR,
                        scope = scope,
                        years = years,
                    )
                )
                val result = forecast.toResultUi(
                    monthlyVolume = monthlyVolume,
                    years = years,
                    greenGasName = greenGas.name,
                    scopeLabel = state.scope,
                )
                _uiState.update { it.copy(isLoading = false, step = CalculatorStep.RESULT, result = result) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to calculate the reduction", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    fun onFinishClicked() {
        _uiState.update { CalculatorUiState(gases = it.gases) }
    }

    private fun goTo(step: CalculatorStep, error: String?) {
        _uiState.update {
            if (error == null) it.copy(step = step, errorMessage = null) else it.copy(errorMessage = error)
        }
    }
}

// The results screen shows tCO2e per month, the forecast covers the whole horizon
private fun ReductionForecast.toResultUi(
    monthlyVolume: Double,
    years: Int,
    greenGasName: String,
    scopeLabel: String,
): CalculatorResultUi {
    val horizonMonths = years * MONTHS_PER_YEAR
    val after = futureVolumeCo2e / horizonMonths
    val neutralized = (monthlyVolume - after).coerceAtLeast(0.0)
    val percent = if (monthlyVolume > 0) neutralized / monthlyVolume * 100 else 0.0

    return CalculatorResultUi(
        emissionBefore = monthlyVolume.roundToInt(),
        emissionAfter = after.roundToInt(),
        neutralized = neutralized.roundToInt(),
        percentOfTotal = percent.roundToInt(),
        greenGasName = greenGasName,
        scopeLabel = scopeLabel,
        periodYears = years,
        axisTicks = axisTicks(max(monthlyVolume, after)),
    )
}

private fun axisTicks(maxValue: Double): List<Int> {
    val step = max(1, ceil(maxValue / (AXIS_TICK_COUNT - 1)).toInt())
    return List(AXIS_TICK_COUNT) { it * step }
}
