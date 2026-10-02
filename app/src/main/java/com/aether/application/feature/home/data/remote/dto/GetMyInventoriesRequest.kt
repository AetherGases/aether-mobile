package com.aether.application.feature.home.data.remote.dto

import com.aether.application.feature.home.domain.model.InventoryStatus

data class GetMyInventoriesRequest(
    val name: String? = null,
    val status : InventoryStatus? = null,
    val skip: Int? = null,
    val take: Int? = null,
)
