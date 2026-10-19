package com.aether.application.feature.reports.domain.repository

import com.aether.application.feature.home.domain.model.Inventory

interface ReportsRepository {

    suspend fun getMyReports(take: Int): List<Inventory>

    suspend fun getPendingReports(take: Int): List<Inventory>
}
