package com.aether.application.feature.calculator.data.repository

import com.aether.application.R
import com.aether.application.core.utils.UiText
import com.aether.application.core.utils.UserFacingException
import com.aether.application.core.utils.uiText
import com.aether.application.feature.calculator.data.remote.CalculatorApi
import com.aether.application.feature.calculator.data.remote.dto.EmissionDto
import com.aether.application.feature.calculator.data.remote.dto.ReductionForecastRequest
import com.aether.application.feature.calculator.domain.model.GasOption
import com.aether.application.feature.calculator.domain.model.ReductionForecast
import com.aether.application.feature.calculator.domain.model.ReductionForecastParams
import com.aether.application.feature.calculator.domain.repository.CalculatorRepository
import com.aether.application.core.network.readErrorMessage
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import java.net.HttpURLConnection.HTTP_FORBIDDEN
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

class CalculatorRepositoryImpl(
    private val calculatorApi: CalculatorApi
) : CalculatorRepository {

    override suspend fun getGases(): List<GasOption> =
        withReadableErrors { calculatorApi.getGases().map { it.toDomain() } }

    override suspend fun forecastReduction(params: ReductionForecastParams): ReductionForecast =
        withReadableErrors {
            calculatorApi.forecastReduction(
                ReductionForecastRequest(
                    years = params.years,
                    currentEmission = EmissionDto(
                        gas = params.currentGasId,
                        volumeCo2e = params.annualVolumeCo2e,
                        scope = params.scope,
                    ),
                    greenGas = params.greenGasId,
                )
            ).toDomain()
        }

    // Only 400 carries a message meant for the user (the validation refusals), the rest is ours
    private suspend fun <T> withReadableErrors(block: suspend () -> T): T =
        try {
            block()
        } catch (e: HttpException) {
            throw UserFacingException(e.toUserMessage(), e)
        } catch (e: IOException) {
            throw UserFacingException(uiText(R.string.calculator_error_no_connection), e)
        }

    private fun HttpException.toUserMessage(): UiText = when (code()) {
        HTTP_BAD_REQUEST -> readErrorMessage()?.let(::uiText) ?: uiText(R.string.calculator_error_invalid_data)
        HTTP_UNAUTHORIZED -> uiText(R.string.calculator_error_session_expired)
        HTTP_FORBIDDEN -> uiText(R.string.calculator_error_forbidden)
        else -> uiText(R.string.calculator_error_generic)
    }
}
