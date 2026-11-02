package com.aether.application.feature.home.data.remote.dto

import com.aether.application.feature.reports.domain.model.ReportsPage
import kotlinx.serialization.Serializable

/** Response of the `history` route; the items have the same shape as the department lists. */
@Serializable
data class GetInventoryHistoryResponse(
    val inventories: List<InventoryDto>,
    val totalCount: Long
) {
    fun toDomain() = ReportsPage(inventories.map { it.toDomain() }, totalCount)
}
