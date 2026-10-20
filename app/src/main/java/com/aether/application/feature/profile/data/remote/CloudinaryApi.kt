package com.aether.application.feature.profile.data.remote

import com.aether.application.feature.profile.data.remote.dto.UploadSignatureResponse
import retrofit2.http.GET
import retrofit2.http.Query

private const val CLOUDINARY = "cloudinary"

/** ms-cloudinary: signs uploads so the app never holds the Cloudinary secret. */
interface CloudinaryApi {

    @GET("$CLOUDINARY/upload-signature")
    suspend fun getUploadSignature(
        @Query("fileType") fileType: String
    ): UploadSignatureResponse
}
