package com.aether.application.feature.home.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Inventory(
    val id: Long,
    val name: String,
    val createdAt: LocalDateTime,
    /** Null when the inventory has no reporting period set. */
    val period: ReportingPeriod?,
    /** Null on the user's own list, where the author is always the user. */
    val author: String?,
    val authorImageUrl: String?,
    /** TODO: not sent by the backend yet. */
    val reviewer: String?,
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

/**
 * Builds the status from the backend's `status` and `type` (INPUT or OUTPUT) fields.
 * PROCESSING_FAILED exists in both enums, so [type] decides which one it is.
 */
fun inventoryStatusOf(status: String, type: String): InventoryStatus {
    val inputs = InventoryStatus.Input.entries.firstOrNull { it.name == status }
    val outputs = InventoryStatus.Output.entries.firstOrNull { it.name == status }
    val preferred = if (type.equals("OUTPUT", ignoreCase = true)) outputs else inputs
    return preferred ?: inputs ?: outputs
        ?: throw IllegalArgumentException("Unknown inventory status: $status ($type)")
}
