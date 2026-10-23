package com.aether.application.feature.reports.data.remote

import com.aether.application.feature.reports.data.remote.dto.GetImprovementPlanResponse
import com.aether.application.feature.reports.data.remote.dto.GetReportAnalysisResponse
import com.aether.application.feature.reports.data.remote.dto.GetReportExtrasResponse
import com.aether.application.feature.reports.data.remote.dto.GetReportResponse
import com.aether.application.feature.reports.data.remote.dto.RegisterReportRequest
import com.aether.application.feature.reports.data.remote.dto.RegisterReportResponse
import com.aether.application.feature.reports.data.remote.dto.ReviewReportRequest
import com.aether.application.feature.reports.data.remote.dto.UploadSignatureResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Kong forwards /api/inventories to ms-inventory without stripping the prefix
private const val INVENTORY = "api/inventories"

interface ReportsApi {

    /** Asks ms-cloudinary to sign an upload, so the file can go straight to Cloudinary. */
    @GET("api/cloudinary/upload-signature")
    suspend fun getUploadSignature(@Query("fileType") fileType: String): UploadSignatureResponse

    @POST(INVENTORY)
    suspend fun registerReport(@Body request: RegisterReportRequest): RegisterReportResponse

    /** A report the logged user owns. */
    @GET("$INVENTORY/mine/{id}")
    suspend fun getMyReport(@Path("id") id: Long): GetReportResponse

    /** Any report of the reviewer's department, whatever its status (pending ones included). */
    @GET("$INVENTORY/reviewed/{id}")
    suspend fun getDepartmentReport(@Path("id") id: Long): GetReportResponse

    // TODO: guessed endpoints, ms-inventory has no route yet that returns the emissions or the analysis
    @GET("$INVENTORY/{id}/emissions")
    suspend fun getReportExtras(@Path("id") id: Long): GetReportExtrasResponse

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

    // Served by ms-aeko-hub; the gateway strips the /hub prefix
    @GET("hub/ai/report/{id}")
    suspend fun getImprovementPlan(@Path("id") inventoryId: Long): GetImprovementPlanResponse
}
