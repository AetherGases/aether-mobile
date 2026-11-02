package com.aether.application.feature.calculator.data.remote

import com.aether.application.feature.calculator.data.remote.dto.GasCatalogItemResponse
import com.aether.application.feature.calculator.data.remote.dto.ReductionForecastRequest
import com.aether.application.feature.calculator.data.remote.dto.ReductionForecastResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// Kong forwards /api/calculator to ms-calculator without stripping the prefix
private const val CALCULATOR = "api/calculator"

interface CalculatorApi {

    @GET("$CALCULATOR/gases")
    suspend fun getGases(
        @Query("name") name: String? = null,
        @Query("isGreen") isGreen: Boolean? = null,
    ): List<GasCatalogItemResponse>

    @POST("$CALCULATOR/reduction/forecast")
    suspend fun forecastReduction(@Body request: ReductionForecastRequest): ReductionForecastResponse
}
