package com.aether.application.feature.home.data.remote.dto

import com.aether.application.feature.home.domain.model.Inventory
import kotlinx.serialization.Serializable

@Serializable
data class GetMyInventoriesResponse(
    val inventories: List<Inventory>,
    val totalCount: Long
)
