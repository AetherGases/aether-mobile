package com.aether.application.feature.home.data.repository

import com.aether.application.feature.home.data.remote.InventoryApi
import com.aether.application.feature.home.domain.model.EnterpriseInfo
import com.aether.application.feature.home.domain.model.Inventory
import com.aether.application.feature.home.domain.model.InventoryCount
import com.aether.application.feature.home.domain.model.SealProgress
import com.aether.application.feature.home.domain.model.UnitInfo
import com.aether.application.feature.home.domain.repository.HomeRepository

class HomeRepositoryImpl(
    private val inventoryApi: InventoryApi
): HomeRepository {

    override suspend fun getMyInventories(take: Int): List<Inventory> {
        val response = inventoryApi.getMyInventories(take = take)
        return response.inventories.map { it.toDomain() }
    }

    override suspend fun getPendingInventories(take: Int): List<Inventory> {
        val response = inventoryApi.getPendingInventories(take = take)
        return response.inventories.map { it.toDomain() }
    }

    // TODO: no backend route yet for unit and enterprise info, see InventoryApi
    override suspend fun getMonthlyUnitInfos(): UnitInfo =
        inventoryApi.getUnitInfo().toDomain()

    override suspend fun getEnterpriseInfos(): EnterpriseInfo =
        inventoryApi.getEnterpriseInfo().toDomain()

    override suspend fun getLastReviewedInventory(): Inventory =
        inventoryApi.getLastReviewedInventory().toDomain()

    override suspend fun getLastSubmittedInventory(): Inventory =
        inventoryApi.getLastSubmittedInventory().toDomain()

    override suspend fun getInventoryCount(): InventoryCount =
        inventoryApi.countInventories().toDomain()

    override suspend fun getSealProgress(): SealProgress =
        inventoryApi.getSealProgress().toDomain()
}
