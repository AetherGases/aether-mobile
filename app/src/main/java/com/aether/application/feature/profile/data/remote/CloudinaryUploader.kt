package com.aether.application.feature.profile.data.remote

import com.aether.application.feature.profile.data.remote.dto.CloudinaryUploadResponse
import com.aether.application.feature.profile.data.remote.dto.UploadSignatureResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

private const val CLOUDINARY_UPLOAD_URL = "https://api.cloudinary.com/v1_1"

/**
 * Sends files straight to Cloudinary with a signature from ms-cloudinary.
 * Uses its own [OkHttpClient] so the gateway token and base url interceptors never reach Cloudinary.
 */
class CloudinaryUploader(
    private val okHttpClient: OkHttpClient = OkHttpClient()
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun upload(
        signature: UploadSignatureResponse,
        fileName: String,
        bytes: ByteArray,
        mimeType: String
    ): CloudinaryUploadResponse = withContext(Dispatchers.IO) {
        // Only the signed params (timestamp, folder) plus api_key and file may be sent
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", fileName, bytes.toRequestBody(mimeType.toMediaType()))
            .addFormDataPart("api_key", signature.apiKey)
            .addFormDataPart("timestamp", signature.timestamp.toString())
            .addFormDataPart("folder", signature.cloudinaryFolder)
            .addFormDataPart("signature", signature.signature)
            .build()

        val request = Request.Builder()
            .url("$CLOUDINARY_UPLOAD_URL/${signature.cloudName}/${signature.cloudinaryResourceType}/upload")
            .post(body)
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            val responseBody = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IOException("Cloudinary upload failed (${response.code}): $responseBody")
            }
            json.decodeFromString<CloudinaryUploadResponse>(responseBody)
        }
    }
}
