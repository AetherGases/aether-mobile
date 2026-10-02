package com.aether.application.feature.home.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Inventory(
    val id: Long,
    val name: String,
    val createdAt: LocalDateTime,
    val period: ReportingPeriod,
    val author: String?,
    val authorImageUrl: String?,
    val status: InventoryStatus,
)

data class ReportingPeriod(
    val start: LocalDate,
    val end: LocalDate
)

sealed interface InventoryStatus {

    enum class Input : InventoryStatus {
        UNDER_REVIEW,
        APPROVED,
        REJECTED,
        PROCESSING,
        PROCESSING_FAILED,
    }

    enum class Output : InventoryStatus {
        REQUESTED,
        ISSUED,
        PROCESSING_FAILED,
    }
}