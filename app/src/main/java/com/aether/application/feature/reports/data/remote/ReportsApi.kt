package com.aether.application.feature.reports.data.remote

import com.aether.application.feature.reports.data.remote.dto.RegisterReportRequest
import com.aether.application.feature.reports.data.remote.dto.RegisterReportResponse
import com.aether.application.feature.reports.data.remote.dto.UploadSignatureResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ReportsApi {

    /** Asks ms-cloudinary to sign an upload, so the file can go straight to Cloudinary. */
    @GET("api/cloudinary/upload-signature")
    suspend fun getUploadSignature(@Query("fileType") fileType: String): UploadSignatureResponse

    @POST("api/inventories")
    suspend fun registerReport(@Body request: RegisterReportRequest): RegisterReportResponse
}
