package com.aether.application.feature.reports.data.remote

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.aether.application.feature.reports.data.remote.dto.UploadSignatureResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.BufferedSink
import okio.source
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val UPLOAD_URL = "https://api.cloudinary.com/v1_1/%s/%s/upload"
private const val CHUNK_SIZE = 8L * 1024

/**
 * Sends a file from the device to Cloudinary using a signature issued by ms-cloudinary.
 * [client] must be a plain OkHttpClient: the app's own client would add our access token.
 */
class CloudinaryUploader(
    private val contentResolver: ContentResolver,
    private val client: OkHttpClient
) {

    fun displayName(uri: Uri): String? =
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }

    /** Returns the `secure_url` of the uploaded file. */
    suspend fun upload(
        uri: Uri,
        fileName: String,
        signature: UploadSignatureResponse,
        onProgress: (Float) -> Unit
    ): String {
        val file = ContentUriBody(
            contentResolver = contentResolver,
            uri = uri,
            contentType = SPREADSHEET_MEDIA_TYPE,
            onProgress = onProgress
        )
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("api_key", signature.apiKey)
            .addFormDataPart("timestamp", signature.timestamp.toString())
            .addFormDataPart("signature", signature.signature)
            .addFormDataPart("folder", signature.cloudinaryFolder)
            .addFormDataPart("file", fileName, file)
            .build()
        val request = Request.Builder()
            .url(UPLOAD_URL.format(signature.cloudName, signature.cloudinaryResourceType))
            .post(body)
            .build()

        return client.newCall(request).await().use { response ->
            val json = response.body?.string().orEmpty()
            if (!response.isSuccessful) throw IOException(errorMessage(json) ?: "HTTP ${response.code}")
            Json.parseToJsonElement(json).jsonObject["secure_url"]?.jsonPrimitive?.contentOrNull
                ?: throw IOException("Cloudinary did not return the file URL")
        }
    }

    private fun errorMessage(json: String): String? = try {
        (Json.parseToJsonElement(json).jsonObject["error"] as? JsonObject)
            ?.get("message")?.jsonPrimitive?.contentOrNull
    } catch (_: Exception) {
        null
    }

    private suspend fun Call.await(): Response = suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation { cancel() }
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) = continuation.resume(response)
            override fun onFailure(call: Call, e: IOException) = continuation.resumeWithException(e)
        })
    }

    /** Streams a content Uri and reports how much of it was written. */
    private class ContentUriBody(
        private val contentResolver: ContentResolver,
        private val uri: Uri,
        private val contentType: MediaType,
        private val onProgress: (Float) -> Unit
    ) : RequestBody() {

        override fun contentType() = contentType

        override fun contentLength(): Long =
            contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
                ?.use { cursor -> if (cursor.moveToFirst() && !cursor.isNull(0)) cursor.getLong(0) else -1L }
                ?: -1L

        override fun writeTo(sink: BufferedSink) {
            val total = contentLength()
            val input = contentResolver.openInputStream(uri) ?: throw IOException("Cannot open $uri")
            input.source().use { source ->
                var written = 0L
                while (true) {
                    val read = source.read(sink.buffer, CHUNK_SIZE)
                    if (read == -1L) break
                    sink.emitCompleteSegments()
                    written += read
                    if (total > 0) onProgress(written.toFloat() / total)
                }
            }
        }
    }

    private companion object {
        val SPREADSHEET_MEDIA_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaType()
    }
}
