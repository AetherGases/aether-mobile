package com.aether.application.feature.reports.data.remote.dto

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
    val consolidationApproach: String,
    val fileName: String,
    val path: String,
)

@Serializable
data class RegisterReportResponse(
    val id: Long,
    val name: String,
)
