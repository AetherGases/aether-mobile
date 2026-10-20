package com.aether.application.feature.home.data.remote

import com.aether.application.feature.home.data.remote.dto.GetEnterpriseInfoResponse
import com.aether.application.feature.home.data.remote.dto.GetInventoryCountResponse
import com.aether.application.feature.home.data.remote.dto.GetMyInventoriesRequest
import com.aether.application.feature.home.data.remote.dto.GetMyInventoriesResponse
import com.aether.application.feature.home.data.remote.dto.GetPendingInventoriesRequest
import com.aether.application.feature.home.data.remote.dto.GetPendingInventoriesResponse
import com.aether.application.feature.home.data.remote.dto.GetSealProgressResponse
import com.aether.application.feature.home.data.remote.dto.GetUnitInfoResponse
import com.aether.application.feature.home.domain.model.Inventory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Kong forwards /api/inventories to ms-inventory without stripping the prefix
private const val INVENTORY = "api/inventories"

interface InventoryApi {

    @POST("$INVENTORY/mine")
    suspend fun getMyInventories(
        @Body request: GetMyInventoriesRequest
    ): GetMyInventoriesResponse

    @POST("$INVENTORY/pending")
    suspend fun getPendingInventories(
        @Body request: GetPendingInventoriesRequest
    ): GetPendingInventoriesResponse

    // TODO: every endpoint below is a guess, confirm paths and payloads with the backend
    @GET("$INVENTORY/unit-info")
    suspend fun getUnitInfo(): GetUnitInfoResponse

    @GET("$INVENTORY/enterprise-info")
    suspend fun getEnterpriseInfo(): GetEnterpriseInfoResponse

    @GET("$INVENTORY/reviewed/last")
    suspend fun getLastReviewedInventory(): Inventory

    @GET("$INVENTORY/mine/last")
    suspend fun getLastSubmittedInventory(): Inventory

    @GET("$INVENTORY/count")
    suspend fun countInventories(): GetInventoryCountResponse

    @GET("seal/progress")
    suspend fun getSealProgress(): GetSealProgressResponse

}
