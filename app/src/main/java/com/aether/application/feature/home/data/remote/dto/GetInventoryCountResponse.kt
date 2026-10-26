package com.aether.application.feature.home.data.remote.dto

import com.aether.application.feature.home.domain.model.InventoryCount
import kotlinx.serialization.Serializable

@Serializable
data class GetInventoryCountResponse(
    val submittedCount: Int,
    val reviewedCount: Int,
) {
    fun toDomain() = InventoryCount(
        submittedCount = submittedCount,
        reviewedCount = reviewedCount,
    )
}
