package com.aether.application.feature.home.data.repository

import android.util.Log
import com.aether.application.feature.home.data.remote.InventoryApi
import com.aether.application.feature.home.domain.model.EnterpriseInfo
import com.aether.application.feature.home.domain.model.Inventory
import com.aether.application.feature.home.domain.model.InventoryCount
import com.aether.application.feature.home.domain.model.SealProgress
import com.aether.application.feature.home.domain.model.UnitInfo
import com.aether.application.feature.home.domain.repository.HomeRepository
import retrofit2.HttpException
import java.net.HttpURLConnection.HTTP_NOT_FOUND

private const val TAG = "HomeRepository"

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

    override suspend fun getLastReviewedInventory(): Inventory? =
        orNullWhenNotFound { inventoryApi.getLastReviewedInventory().toDomain() }

    override suspend fun getLastSubmittedInventory(): Inventory? =
        orNullWhenNotFound { inventoryApi.getLastSubmittedInventory().toDomain() }

    // The last routes answer 404 while the user has no inventory yet
    private suspend fun <T> orNullWhenNotFound(block: suspend () -> T): T? =
        try {
            block()
        } catch (e: HttpException) {
            if (e.code() != HTTP_NOT_FOUND) throw e
            Log.d(TAG, "No inventory yet (404)")
            null
        }

    override suspend fun getInventoryCount(): InventoryCount =
        inventoryApi.countInventories().toDomain()

    // The count is the totalCount of a one-item page
    override suspend fun getHistoryCount(): Long =
        inventoryApi.getInventoryHistory(take = 1).totalCount

    override suspend fun getSealProgress(): SealProgress =
        inventoryApi.getSealProgress().toDomain()
}
