package com.aether.application.feature.reports.domain.repository

import com.aether.application.feature.reports.domain.model.ImprovementPlan
import com.aether.application.feature.reports.domain.model.ReportAnalysis
import com.aether.application.feature.reports.domain.model.ReportDetails

interface ReportsRepository {

    suspend fun getReport(id: Long): ReportDetails

    suspend fun getReportAnalysis(id: Long): ReportAnalysis

    /** Returns null when Aeko has no plan stored for the report. */
    suspend fun getImprovementPlan(id: Long): ImprovementPlan?

    suspend fun approveReport(id: Long, comment: String)

    suspend fun rejectReport(id: Long, reason: String)
}
