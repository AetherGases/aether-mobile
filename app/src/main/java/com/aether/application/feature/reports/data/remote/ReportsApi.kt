package com.aether.application.feature.reports.data.remote

import com.aether.application.feature.reports.data.remote.dto.GetReportAnalysisResponse
import com.aether.application.feature.reports.data.remote.dto.GetReportResponse
import com.aether.application.feature.reports.data.remote.dto.RegisterReportRequest
import com.aether.application.feature.reports.data.remote.dto.RegisterReportResponse
import com.aether.application.feature.reports.data.remote.dto.ReviewReportRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

// Kong forwards /api/inventories to ms-inventory without stripping the prefix
private const val INVENTORY = "api/inventories"

/** The ms-inventory routes of a single report; the lists live in InventoryApi. */
interface ReportsApi {

    @POST(INVENTORY)
    suspend fun registerReport(@Body request: RegisterReportRequest): RegisterReportResponse

    /** A report the logged user owns. */
    @GET("$INVENTORY/mine/{id}")
    suspend fun getMyReport(@Path("id") id: Long): GetReportResponse

    /** Any report of the reviewer's department, whatever its status (pending ones included). */
    @GET("$INVENTORY/reviewed/{id}")
    suspend fun getDepartmentReport(@Path("id") id: Long): GetReportResponse

    // TODO: guessed endpoint, ms-inventory has no route yet that returns the analysis
    @GET("$INVENTORY/{id}/analysis")
    suspend fun getReportAnalysis(@Path("id") id: Long): GetReportAnalysisResponse

    @PATCH("$INVENTORY/pending/{id}/approve")
    suspend fun approveReport(
        @Path("id") id: Long,
        @Body request: ReviewReportRequest
    )

    @PATCH("$INVENTORY/pending/{id}/reject")
    suspend fun rejectReport(
        @Path("id") id: Long,
        @Body request: ReviewReportRequest
    )
}
