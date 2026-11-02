package com.aether.application.feature.reports.data.repository

import android.util.Log
import android.net.Uri
import com.aether.application.R
import com.aether.application.core.network.cloudinary.CloudinaryApi
import com.aether.application.core.network.cloudinary.CloudinaryFileType
import com.aether.application.core.network.cloudinary.CloudinaryUploader
import com.aether.application.core.network.readErrorMessage
import com.aether.application.core.utils.UiText
import com.aether.application.core.utils.uiText
import com.aether.application.feature.reports.data.remote.ReportsApi
import com.aether.application.feature.reports.data.remote.dto.RegisterReportRequest
import com.aether.application.feature.reports.domain.model.ConsolidationApproach
import com.aether.application.feature.reports.domain.model.CreatedReport
import com.aether.application.feature.reports.domain.model.ReportException
import com.aether.application.feature.reports.domain.repository.NewReportRepository
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "NewReportRepository"
private const val SPREADSHEET_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

class NewReportRepositoryImpl(
    private val reportsApi: ReportsApi,
    private val cloudinaryApi: CloudinaryApi,
    private val uploader: CloudinaryUploader
) : NewReportRepository {

    override fun displayName(uri: String): String? =
        try {
            uploader.displayName(Uri.parse(uri))
        } catch (e: Exception) {
            Log.w(TAG, "Failed to read the name of $uri", e)
            null
        }

    override suspend fun uploadSpreadsheet(
        uri: String,
        fileName: String,
        onProgress: (Float) -> Unit
    ): String = call(uiText(R.string.reports_error_upload_failed)) {
        val signature = cloudinaryApi.getUploadSignature(CloudinaryFileType.SPREADSHEET)
        uploader.upload(Uri.parse(uri), fileName, SPREADSHEET_MIME_TYPE, signature, onProgress)
    }

    override suspend fun createReport(
        name: String,
        approach: ConsolidationApproach,
        fileName: String,
        fileUrl: String
    ): CreatedReport = call(uiText(R.string.reports_error_create_failed)) {
        val response = reportsApi.registerReport(
            RegisterReportRequest(
                name = name,
                consolidationApproach = approach,
                fileName = fileName,
                path = fileUrl
            )
        )
        CreatedReport(id = response.id, name = response.name)
    }

    private suspend fun <T> call(fallbackMessage: UiText, block: suspend () -> T): T =
        try {
            block()
        } catch (e: HttpException) {
            Log.e(TAG, "Request failed (${e.code()})", e)
            // Only 4xx messages are written for the user; a 5xx carries the raw exception text
            val serverMessage = if (e.code() in 400..499) e.readErrorMessage() else null
            throw ReportException(serverMessage?.let(::uiText) ?: fallbackMessage, e)
        } catch (e: IOException) {
            Log.e(TAG, "Request failed", e)
            throw ReportException(uiText(R.string.common_error_no_connection_retry), e)
        }
}
