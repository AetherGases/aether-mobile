package com.aether.application.feature.home.data.repository

import com.aether.application.feature.home.data.remote.InventoryApi
import com.aether.application.feature.home.data.remote.dto.GetMyInventoriesRequest
import com.aether.application.feature.home.data.remote.dto.GetPendingInventoriesRequest
import com.aether.application.feature.home.domain.model.Inventory
import com.aether.application.feature.home.domain.repository.HomeRepository

class HomeRepositoryImpl(
    private val inventoryApi: InventoryApi
): HomeRepository {

    override suspend fun getMyInventories(take: Int): List<Inventory> {
        val response = inventoryApi.getMyInventories(
            request = GetMyInventoriesRequest(take = take)
        )
        return response.inventories
    }

    override suspend fun getPendingInventories(take: Int): List<Inventory> {
        val response = inventoryApi.getPendingInventories(
            request = GetPendingInventoriesRequest(take = take)
        )
        return response.inventories
    }

}