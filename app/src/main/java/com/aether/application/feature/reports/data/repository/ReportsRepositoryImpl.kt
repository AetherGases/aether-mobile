package com.aether.application.feature.reports.data.repository

import com.aether.application.feature.home.data.remote.InventoryApi
import com.aether.application.feature.reports.domain.model.ReportsPage
import com.aether.application.feature.reports.domain.repository.ReportsRepository

class ReportsRepositoryImpl(
    private val inventoryApi: InventoryApi
) : ReportsRepository {

    override suspend fun getMyReports(skip: Int, take: Int): ReportsPage =
        inventoryApi.getMyInventories(skip = skip, take = take)
            .let { page -> ReportsPage(page.inventories.map { it.toDomain() }, page.totalCount) }

    override suspend fun getPendingReports(skip: Int, take: Int): ReportsPage =
        inventoryApi.getPendingInventories(skip = skip, take = take)
            .let { page -> ReportsPage(page.inventories.map { it.toDomain() }, page.totalCount) }

    override suspend fun getReviewedReports(skip: Int, take: Int): ReportsPage =
        inventoryApi.getReviewedInventories(skip = skip, take = take)
            .let { page -> ReportsPage(page.inventories.map { it.toDomain() }, page.totalCount) }
}
