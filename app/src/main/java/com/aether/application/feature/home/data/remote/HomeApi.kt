package com.aether.application.feature.home.data.remote

import com.aether.application.feature.home.data.remote.dto.GetEnterpriseInfoResponse
import com.aether.application.feature.home.data.remote.dto.GetInventoryCountResponse
import com.aether.application.feature.home.data.remote.dto.GetMyInventoriesResponse
import com.aether.application.feature.home.data.remote.dto.GetPendingInventoriesResponse
import com.aether.application.feature.home.data.remote.dto.GetReviewedInventoriesResponse
import com.aether.application.feature.home.data.remote.dto.GetSealProgressResponse
import com.aether.application.feature.home.data.remote.dto.GetUnitInfoResponse
import com.aether.application.feature.home.data.remote.dto.LastInventoryDto
import retrofit2.http.GET
import retrofit2.http.Query

// Kong forwards /api/inventories to ms-inventory without stripping the prefix
private const val INVENTORY = "api/inventories"

interface InventoryApi {

    // The backend caps `take` at 6 on the three list routes
    @GET("$INVENTORY/mine")
    suspend fun getMyInventories(
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("skip") skip: Int? = null,
        @Query("take") take: Int? = null,
    ): GetMyInventoriesResponse

    @GET("$INVENTORY/pending")
    suspend fun getPendingInventories(
        @Query("name") name: String? = null,
        @Query("skip") skip: Int? = null,
        @Query("take") take: Int? = null,
    ): GetPendingInventoriesResponse

    @GET("$INVENTORY/reviewed")
    suspend fun getReviewedInventories(
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("skip") skip: Int? = null,
        @Query("take") take: Int? = null,
    ): GetReviewedInventoriesResponse

    @GET("$INVENTORY/reviewed/last")
    suspend fun getLastReviewedInventory(): LastInventoryDto

    @GET("$INVENTORY/mine/last")
    suspend fun getLastSubmittedInventory(): LastInventoryDto

    @GET("$INVENTORY/count")
    suspend fun countInventories(): GetInventoryCountResponse

    // TODO: no backend route yet for the endpoints below, the paths and payloads are guesses
    @GET("$INVENTORY/unit-info")
    suspend fun getUnitInfo(): GetUnitInfoResponse

    @GET("$INVENTORY/enterprise-info")
    suspend fun getEnterpriseInfo(): GetEnterpriseInfoResponse

    @GET("$INVENTORY/seal/progress")
    suspend fun getSealProgress(): GetSealProgressResponse

}
