package com.aether.application.feature.reports.data.remote

import com.aether.application.feature.reports.data.remote.dto.GetImprovementPlanResponse
import retrofit2.http.GET
import retrofit2.http.Path

// Served by ms-aeko-hub; the gateway strips the /hub prefix
private const val HUB = "hub"

interface HubApi {

    /** The improvement plan Aeko wrote for an inventory; 404 while there is none. */
    @GET("$HUB/ai/report/{id}")
    suspend fun getImprovementPlan(@Path("id") inventoryId: Long): GetImprovementPlanResponse
}
