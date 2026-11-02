package com.aether.application.feature.reports.data.repository

import android.util.Log
import com.aether.application.feature.home.data.remote.InventoryApi
import com.aether.application.feature.reports.data.remote.HubApi
import com.aether.application.feature.reports.data.remote.ReportsApi
import com.aether.application.feature.reports.data.remote.dto.ReviewReportRequest
import com.aether.application.feature.reports.domain.model.ImprovementPlan
import com.aether.application.feature.reports.domain.model.ReportAnalysis
import com.aether.application.feature.reports.domain.model.ReportDetails
import com.aether.application.feature.reports.domain.model.ReportsPage
import com.aether.application.feature.reports.domain.repository.ReportsRepository
import retrofit2.HttpException
import java.net.HttpURLConnection.HTTP_FORBIDDEN
import java.net.HttpURLConnection.HTTP_NOT_FOUND

private const val TAG = "ReportsRepository"

class ReportsRepositoryImpl(
    private val inventoryApi: InventoryApi,
    private val reportsApi: ReportsApi,
    private val hubApi: HubApi
) : ReportsRepository {

    override suspend fun getPendingReports(skip: Int, take: Int): ReportsPage =
        inventoryApi.getPendingInventories(skip = skip, take = take)
            .let { page -> ReportsPage(page.inventories.map { it.toDomain() }, page.totalCount) }

    override suspend fun getHistory(skip: Int, take: Int): ReportsPage =
        inventoryApi.getInventoryHistory(skip = skip, take = take).toDomain()

    override suspend fun getReport(id: Long, asReviewer: Boolean): ReportDetails {
        // Owners read through mine/{id}, reviewers through their department's reviewed/{id};
        // someone can be both, so the other route is tried when the first one doesn't know the report
        val routes = listOf(reportsApi::getDepartmentReport, reportsApi::getMyReport)
            .let { if (asReviewer) it else it.reversed() }
        val report = try {
            routes[0](id)
        } catch (e: HttpException) {
            if (e.code() != HTTP_NOT_FOUND && e.code() != HTTP_FORBIDDEN) throw e
            Log.d(TAG, "Report $id not readable through the first route (${e.code()}), trying the other one")
            routes[1](id)
        }
        return report.toDomain()
    }

    override suspend fun getReportAnalysis(id: Long): ReportAnalysis =
        reportsApi.getReportAnalysis(id).toDomain()

    override suspend fun getImprovementPlan(id: Long): ImprovementPlan? =
        try {
            hubApi.getImprovementPlan(id).toDomain()
        } catch (e: HttpException) {
            if (e.code() != HTTP_NOT_FOUND) throw e
            Log.d(TAG, "No improvement plan for report $id yet (404)")
            null
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
