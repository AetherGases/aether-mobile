package com.aether.application.feature.home.data.remote.dto

data class GetPendingInventoriesRequest(
    val name: String? = null,
    val skip: Int? = null,
    val take: Int? = null,
)
