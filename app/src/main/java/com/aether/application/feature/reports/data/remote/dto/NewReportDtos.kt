package com.aether.application.feature.reports.data.remote.dto

import com.aether.application.feature.reports.domain.model.ConsolidationApproach
import kotlinx.serialization.Serializable

@Serializable
data class UploadSignatureResponse(
    val signature: String,
    val timestamp: Long,
    val apiKey: String,
    val cloudName: String,
    val cloudinaryFolder: String,
    val cloudinaryResourceType: String,
)

@Serializable
data class RegisterReportRequest(
    val name: String,
    val consolidationApproach: ConsolidationApproach,
    val fileName: String,
    val path: String,
)

@Serializable
data class RegisterReportResponse(
    val id: Long,
    val name: String,
)
