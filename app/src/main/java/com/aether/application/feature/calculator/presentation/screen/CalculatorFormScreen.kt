package com.aether.application.feature.calculator.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.aether.application.feature.calculator.presentation.screen.step.DeadlineStep
import com.aether.application.feature.calculator.presentation.screen.step.EmissionStep
import com.aether.application.feature.calculator.presentation.screen.step.GreenGasStep
import com.aether.application.feature.calculator.presentation.state.CalculatorStep
import com.aether.application.feature.calculator.presentation.state.CalculatorUiState

@Composable
fun CalculatorFormScreen(
    uiState: CalculatorUiState,
    onPollutingGasChanged: (String) -> Unit,
    onMonthlyVolumeChanged: (String) -> Unit,
    onScopeChanged: (String) -> Unit,
    onGreenGasChanged: (String) -> Unit,
    onDeadlineChanged: (String) -> Unit,
    onNextClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onCalculateClicked: () -> Unit,
    onConsultAekoClicked: () -> Unit,
    onFinishClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackClicked)

    Box(modifier = modifier.fillMaxSize()) {
        when (uiState.step) {
            CalculatorStep.EMISSION -> EmissionStep(
                pollutingGasType = uiState.pollutingGas,
                monthlyVolume = uiState.monthlyVolume,
                scope = uiState.scope,
                gasOptions = uiState.pollutingGasOptions,
                onGasTypeChanged = onPollutingGasChanged,
                onVolumeChanged = onMonthlyVolumeChanged,
                onScopeChanged = onScopeChanged,
                onBackClicked = onBackClicked,
                onNextClicked = onNextClicked
            )
            CalculatorStep.GREEN_GAS -> GreenGasStep(
                greenGas = uiState.greenGas,
                gasOptions = uiState.greenGasOptions,
                onGreenGasChanged = onGreenGasChanged,
                onConsultAekoClicked = onConsultAekoClicked,
                onBackClicked = onBackClicked,
                onNextClicked = onNextClicked
            )
            CalculatorStep.DEADLINE -> DeadlineStep(
                deadline = uiState.deadline,
                onDeadlineChanged = onDeadlineChanged,
                onBackClicked = onBackClicked,
                onCalculateClicked = onCalculateClicked
            )
            CalculatorStep.RESULT -> uiState.result?.let { result ->
                CalculatorResultsScreen(
                    result = result,
                    onBackClick = onBackClicked,
                    onFinishClick = onFinishClicked
                )
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
