package com.aether.application.feature.home.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetPendingInventoriesRequest(
    val name: String? = null,
    val skip: Int? = null,
    val take: Int? = null,
)
