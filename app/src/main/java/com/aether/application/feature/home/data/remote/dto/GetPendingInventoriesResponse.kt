package com.aether.application.feature.home.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetPendingInventoriesResponse(
    val inventories: List<InventoryDto>,
    val totalCount: Long,
)
