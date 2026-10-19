package com.aether.application.feature.reports.data.repository

import com.aether.application.feature.reports.data.remote.ReportsApi
import com.aether.application.feature.reports.data.remote.dto.ReviewReportRequest
import com.aether.application.feature.reports.domain.model.ImprovementPlan
import com.aether.application.feature.reports.domain.model.ReportAnalysis
import com.aether.application.feature.reports.domain.model.ReportDetails
import com.aether.application.feature.reports.domain.repository.ReportsRepository
import retrofit2.HttpException

class ReportsRepositoryImpl(
    private val reportsApi: ReportsApi
) : ReportsRepository {

    override suspend fun getReport(id: Long): ReportDetails =
        reportsApi.getReport(id).toDomain()

    override suspend fun getReportAnalysis(id: Long): ReportAnalysis =
        reportsApi.getReportAnalysis(id).toDomain()

    override suspend fun getImprovementPlan(id: Long): ImprovementPlan? =
        try {
            reportsApi.getImprovementPlan(id).toDomain()
        } catch (e: HttpException) {
            if (e.code() == 404) null else throw e
        }

    override suspend fun approveReport(id: Long, comment: String) {
        reportsApi.approveReport(
            id = id,
            request = ReviewReportRequest(message = comment)
        )
    }

    override suspend fun rejectReport(id: Long, reason: String) {
        reportsApi.rejectReport(
            id = id,
            request = ReviewReportRequest(message = reason)
        )
    }
}
