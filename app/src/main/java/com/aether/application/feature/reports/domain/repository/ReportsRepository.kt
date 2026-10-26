package com.aether.application.feature.reports.domain.repository

import com.aether.application.feature.reports.domain.model.ImprovementPlan
import com.aether.application.feature.reports.domain.model.ReportAnalysis
import com.aether.application.feature.reports.domain.model.ReportDetails
import com.aether.application.feature.reports.domain.model.ReportsPage

interface ReportsRepository {

    suspend fun getMyReports(skip: Int, take: Int): ReportsPage

    suspend fun getReport(id: Long, asReviewer: Boolean): ReportDetails

    suspend fun getReportAnalysis(id: Long): ReportAnalysis

    suspend fun getPendingReports(skip: Int, take: Int): ReportsPage

    suspend fun getImprovementPlan(id: Long): ImprovementPlan?

    suspend fun getReviewedReports(skip: Int, take: Int): ReportsPage

    suspend fun approveReport(id: Long, comment: String)

    suspend fun rejectReport(id: Long, reason: String)
}
