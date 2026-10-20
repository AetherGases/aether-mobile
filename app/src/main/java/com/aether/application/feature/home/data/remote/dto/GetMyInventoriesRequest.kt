package com.aether.application.feature.home.data.remote.dto

import com.aether.application.feature.home.domain.model.InventoryStatus
import com.aether.application.feature.home.domain.model.InventoryStatusSerializer
import kotlinx.serialization.Serializable

@Serializable
data class GetMyInventoriesRequest(
    val name: String? = null,
    @Serializable(with = InventoryStatusSerializer::class)
    val status: InventoryStatus? = null,
    val skip: Int? = null,
    val take: Int? = null,
)
