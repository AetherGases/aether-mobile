package com.aether.application.feature.reports.domain.repository

import com.aether.application.feature.reports.domain.model.ConsolidationApproach
import com.aether.application.feature.reports.domain.model.CreatedReport

interface NewReportRepository {

    /** The file name the user sees for [uri], or null when it can't be read. */
    fun displayName(uri: String): String?

    /**
     * Uploads the spreadsheet to the file storage and returns its public URL.
     * [onProgress] receives values from 0 to 1 while the bytes are sent.
     */
    suspend fun uploadSpreadsheet(
        uri: String,
        fileName: String,
        onProgress: (Float) -> Unit
    ): String

    suspend fun createReport(
        name: String,
        approach: ConsolidationApproach,
        fileName: String,
        fileUrl: String
    ): CreatedReport
}
