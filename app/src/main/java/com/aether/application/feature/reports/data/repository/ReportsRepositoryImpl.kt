package com.aether.application.feature.reports.data.repository

import android.util.Log
import com.aether.application.feature.home.data.remote.InventoryApi
import com.aether.application.feature.reports.data.remote.ReportsApi
import com.aether.application.feature.reports.data.remote.dto.ReviewReportRequest
import com.aether.application.feature.reports.domain.model.ImprovementPlan
import com.aether.application.feature.reports.domain.model.ReportAnalysis
import com.aether.application.feature.reports.domain.model.ReportDetails
import com.aether.application.feature.reports.domain.model.ReportsPage
import com.aether.application.feature.reports.domain.repository.ReportsRepository
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.net.HttpURLConnection.HTTP_FORBIDDEN
import java.net.HttpURLConnection.HTTP_NOT_FOUND

private const val TAG = "ReportsRepository"

class ReportsRepositoryImpl(
    private val inventoryApi: InventoryApi,
    private val reportsApi: ReportsApi
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

    override suspend fun getReport(id: Long, asReviewer: Boolean): ReportDetails {
        // Owners read through mine/{id}, reviewers through their department's reviewed/{id};
        // someone can be both, so the other route is tried when the first one doesn't know the report
        val routes = listOf(reportsApi::getDepartmentReport, reportsApi::getMyReport)
            .let { if (asReviewer) it else it.reversed() }
        val report = try {
            routes[0](id)
        } catch (e: HttpException) {
            if (e.code() != HTTP_NOT_FOUND && e.code() != HTTP_FORBIDDEN) throw e
            routes[1](id)
        }
        return report.toDomain(extras = getReportExtras(id))
    }

    // Emissions, sector and unit come from a guessed endpoint, so the report still shows without them
    private suspend fun getReportExtras(id: Long) =
        try {
            reportsApi.getReportExtras(id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Report $id extras unavailable", e)
            null
        }

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
