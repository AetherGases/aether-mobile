package com.aether.application.feature.home.data.remote

import com.aether.application.feature.home.data.remote.dto.GetMyInventoriesRequest
import com.aether.application.feature.home.data.remote.dto.GetMyInventoriesResponse
import com.aether.application.feature.home.data.remote.dto.GetPendingInventoriesRequest
import com.aether.application.feature.home.data.remote.dto.GetPendingInventoriesResponse
import retrofit2.http.Body
import retrofit2.http.POST

private const val INVENTORY = "inventories"

interface InventoryApi {

    @POST("$INVENTORY/mine")
    suspend fun getMyInventories(
        @Body request: GetMyInventoriesRequest
    ): GetMyInventoriesResponse

    @POST("$INVENTORY/pending")
    suspend fun getPendingInventories(
        @Body request: GetPendingInventoriesRequest
    ): GetPendingInventoriesResponse

}