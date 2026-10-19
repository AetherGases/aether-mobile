package com.aether.application.feature.reports.data.repository

import android.net.Uri
import com.aether.application.core.network.readErrorMessage
import com.aether.application.feature.reports.data.remote.CloudinaryUploader
import com.aether.application.feature.reports.data.remote.ReportsApi
import com.aether.application.feature.reports.data.remote.dto.RegisterReportRequest
import com.aether.application.feature.reports.domain.model.ConsolidationApproach
import com.aether.application.feature.reports.domain.model.CreatedReport
import com.aether.application.feature.reports.domain.model.ReportException
import com.aether.application.feature.reports.domain.repository.NewReportRepository
import retrofit2.HttpException
import java.io.IOException

// ms-cloudinary file type for spreadsheets, see StorageFileTypeEnum
private const val SPREADSHEET_FILE_TYPE = "XLSX"

class NewReportRepositoryImpl(
    private val reportsApi: ReportsApi,
    private val uploader: CloudinaryUploader
) : NewReportRepository {

    override fun displayName(uri: String): String? =
        try {
            uploader.displayName(Uri.parse(uri))
        } catch (_: Exception) {
            null
        }

    override suspend fun uploadSpreadsheet(
        uri: String,
        fileName: String,
        onProgress: (Float) -> Unit
    ): String = call("Não foi possível enviar o arquivo. Tente novamente.") {
        val signature = reportsApi.getUploadSignature(SPREADSHEET_FILE_TYPE)
        uploader.upload(Uri.parse(uri), fileName, signature, onProgress)
    }

    override suspend fun createReport(
        name: String,
        approach: ConsolidationApproach,
        fileName: String,
        fileUrl: String
    ): CreatedReport = call("Não foi possível criar o relatório. Tente novamente.") {
        val response = reportsApi.registerReport(
            RegisterReportRequest(
                name = name,
                consolidationApproach = approach.label,
                fileName = fileName,
                path = fileUrl
            )
        )
        CreatedReport(id = response.id, name = response.name)
    }

    private suspend fun <T> call(fallbackMessage: String, block: suspend () -> T): T =
        try {
            block()
        } catch (e: HttpException) {
            throw ReportException(e.readErrorMessage() ?: fallbackMessage, e)
        } catch (e: IOException) {
            throw ReportException("Sem conexão com o servidor. Tente novamente.", e)
        }
}
