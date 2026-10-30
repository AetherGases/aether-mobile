package com.aether.application.feature.calculator.data.repository

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
            throw Exception(e.toUserMessage(), e)
        } catch (e: IOException) {
            throw Exception("Sem conexão com o servidor. Verifique sua internet e tente novamente.", e)
        }

    private fun HttpException.toUserMessage(): String = when (code()) {
        HTTP_BAD_REQUEST -> readErrorMessage() ?: "Os dados informados são inválidos."
        HTTP_UNAUTHORIZED -> "Sua sessão expirou. Entre novamente para usar a calculadora."
        HTTP_FORBIDDEN -> "Você não tem permissão para usar a calculadora."
        else -> "Não foi possível calcular agora. Tente novamente mais tarde."
    }
}
