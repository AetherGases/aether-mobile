package com.aether.application.feature.reports.data.remote

import com.aether.application.feature.reports.data.remote.dto.UploadSignatureResponse
import retrofit2.http.GET
import retrofit2.http.Query

/** ms-cloudinary, our own service; the upload itself goes straight to Cloudinary through [CloudinaryUploader]. */
interface CloudinaryApi {

    /** Asks ms-cloudinary to sign an upload, so the file can go straight to Cloudinary. */
    @GET("api/cloudinary/upload-signature")
    suspend fun getUploadSignature(@Query("fileType") fileType: String): UploadSignatureResponse
}
