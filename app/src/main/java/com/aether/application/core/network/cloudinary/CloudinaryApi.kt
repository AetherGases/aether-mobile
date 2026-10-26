package com.aether.application.core.network.cloudinary

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

/** ms-cloudinary file types, see StorageFileTypeEnum. */
object CloudinaryFileType {
    const val IMAGE = "IMAGE"
    const val SPREADSHEET = "XLSX"
}

/** ms-cloudinary, our own service; the upload itself goes straight to Cloudinary through [CloudinaryUploader]. */
interface CloudinaryApi {

    /** Asks ms-cloudinary to sign an upload, so the app never holds the Cloudinary secret. */
    @GET("api/cloudinary/upload-signature")
    suspend fun getUploadSignature(@Query("fileType") fileType: String): UploadSignatureResponse
}

@Serializable
data class UploadSignatureResponse(
    val signature: String,
    val timestamp: Long,
    val apiKey: String,
    val cloudName: String,
    val cloudinaryFolder: String,
    val cloudinaryResourceType: String,
)
