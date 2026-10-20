package com.aether.application.feature.reports.domain.repository

import com.aether.application.feature.reports.domain.model.ImprovementPlan
import com.aether.application.feature.reports.domain.model.ReportAnalysis
import com.aether.application.feature.reports.domain.model.ReportDetails

interface ReportsRepository {

    /** [asReviewer] picks the route to try first: the reviewer's department or the user's own reports. */
    suspend fun getReport(id: Long, asReviewer: Boolean): ReportDetails

    suspend fun getReportAnalysis(id: Long): ReportAnalysis

    /** Returns null when Aeko has no plan stored for the report. */
    suspend fun getImprovementPlan(id: Long): ImprovementPlan?

    suspend fun approveReport(id: Long, comment: String)

    suspend fun rejectReport(id: Long, reason: String)
}
