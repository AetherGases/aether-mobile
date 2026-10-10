package com.aether.application.feature.home.data.remote.dto

import com.aether.application.feature.home.domain.model.EnterpriseInfo
import kotlinx.serialization.Serializable

@Serializable
data class GetEnterpriseInfoResponse(
    val totalReductions: Int,
    val totalEmissions: Int,
) {
    fun toDomain() = EnterpriseInfo(
        totalReductions = totalReductions,
        totalEmissions = totalEmissions,
    )
}
