package com.aether.application.feature.home.data.remote.dto

import com.aether.application.feature.home.domain.model.UnitInfo
import kotlinx.serialization.Serializable

@Serializable
data class GetUnitInfoResponse(
    val emissions: Int,
    val reductionPercentage: Int,
) {
    fun toDomain() = UnitInfo(
        emissions = emissions,
        reductionPercentage = reductionPercentage,
    )
}
