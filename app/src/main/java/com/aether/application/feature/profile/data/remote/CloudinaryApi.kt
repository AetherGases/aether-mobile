package com.aether.application.feature.profile.data.remote

import com.aether.application.feature.profile.data.remote.dto.UploadSignatureResponse
import retrofit2.http.GET
import retrofit2.http.Query

// Kong forwards /api/cloudinary to ms-cloudinary without stripping the prefix
private const val CLOUDINARY = "api/cloudinary"

/** ms-cloudinary: signs uploads so the app never holds the Cloudinary secret. */
interface CloudinaryApi {

    @GET("$CLOUDINARY/upload-signature")
    suspend fun getUploadSignature(
        @Query("fileType") fileType: String
    ): UploadSignatureResponse
}
