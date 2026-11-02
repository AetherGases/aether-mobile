package com.aether.application.feature.home.domain.repository

import com.aether.application.feature.home.domain.model.EnterpriseInfo
import com.aether.application.feature.home.domain.model.Inventory
import com.aether.application.feature.home.domain.model.InventoryCount
import com.aether.application.feature.home.domain.model.SealProgress
import com.aether.application.feature.home.domain.model.UnitInfo

interface HomeRepository {

    suspend fun getMyInventories(
        take: Int
    ): List<Inventory>

    suspend fun getPendingInventories(
        take: Int
    ): List<Inventory>

    /** Null while the user has no inventory yet. */
    suspend fun getLastReviewedInventory(): Inventory?
    suspend fun getLastSubmittedInventory(): Inventory?
    suspend fun getMonthlyUnitInfos(): UnitInfo
    suspend fun getEnterpriseInfos(): EnterpriseInfo
    suspend fun getInventoryCount(): InventoryCount

    /** Size of the user's history, the number the summary and hero cards show. */
    suspend fun getHistoryCount(): Long
    suspend fun getSealProgress(): SealProgress
}