package com.aether.application.feature.home.data.remote.dto

import com.aether.application.feature.home.domain.model.Inventory

data class GetPendingInventoriesResponse(
    val inventories: List<Inventory>,
    val totalCount: Long,
)
