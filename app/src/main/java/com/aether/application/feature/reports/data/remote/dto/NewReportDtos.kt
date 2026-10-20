package com.aether.application.feature.reports.data.remote.dto

import com.aether.application.feature.reports.domain.model.ConsolidationApproach
import com.aether.application.core.serialization.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

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

/** Response of ms-inventory `POST /api/inventories`; every field is declared since unknown keys fail decoding. */
@Serializable
data class RegisterReportResponse(
    val id: Long,
    val name: String,
    val type: String,
    val status: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val storageFile: StorageFileDto? = null,
) {
    @Serializable
    data class StorageFileDto(
        val id: Long,
        val fileName: String,
        val path: String,
        @Serializable(with = LocalDateTimeSerializer::class)
        val createdAt: LocalDateTime? = null,
    )
}
