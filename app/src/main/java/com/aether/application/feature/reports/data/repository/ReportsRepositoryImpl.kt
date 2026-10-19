package com.aether.application.feature.reports.data.repository

import com.aether.application.feature.home.data.remote.InventoryApi
import com.aether.application.feature.home.data.remote.dto.GetMyInventoriesRequest
import com.aether.application.feature.home.data.remote.dto.GetPendingInventoriesRequest
import com.aether.application.feature.home.domain.model.Inventory
import com.aether.application.feature.reports.domain.repository.ReportsRepository

class ReportsRepositoryImpl(
    private val inventoryApi: InventoryApi
) : ReportsRepository {

    override suspend fun getMyReports(take: Int): List<Inventory> =
        inventoryApi.getMyInventories(GetMyInventoriesRequest(take = take)).inventories

    override suspend fun getPendingReports(take: Int): List<Inventory> =
        inventoryApi.getPendingInventories(GetPendingInventoriesRequest(take = take)).inventories
}
