package com.aether.application.feature.reports.domain.repository

import com.aether.application.feature.reports.domain.model.ReportsPage

interface ReportsRepository {

    suspend fun getMyReports(skip: Int, take: Int): ReportsPage

    suspend fun getPendingReports(skip: Int, take: Int): ReportsPage

    suspend fun getReviewedReports(skip: Int, take: Int): ReportsPage
}
