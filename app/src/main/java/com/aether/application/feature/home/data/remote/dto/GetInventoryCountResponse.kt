package com.aether.application.feature.home.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetInventoryCountResponse(
    val count: Int,
)
