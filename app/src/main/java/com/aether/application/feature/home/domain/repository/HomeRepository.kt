package com.aether.application.feature.home.domain.repository

import com.aether.application.feature.home.domain.model.EnterpriseInfo
import com.aether.application.feature.home.domain.model.Inventory
import com.aether.application.feature.home.domain.model.UnitInfo

interface HomeRepository {

    suspend fun getMyInventories(
        take: Int
    ): List<Inventory>

    suspend fun getPendingInventories(
        take: Int
    ): List<Inventory>

    suspend fun getLastReviewedInventory(): Inventory
    suspend fun getLastSubmittedInventory(): Inventory
    suspend fun getMonthlyUnitInfos(): UnitInfo
    suspend fun getEnterpriseInfos(): EnterpriseInfo
    suspend fun countAllSubmittedInvetories(): Int
    suspend fun countReviewedInventories(): Int
}