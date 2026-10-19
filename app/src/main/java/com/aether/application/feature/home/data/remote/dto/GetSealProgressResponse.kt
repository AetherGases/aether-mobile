package com.aether.application.feature.home.data.remote.dto

import com.aether.application.feature.home.domain.model.SealProgress
import kotlinx.serialization.Serializable

@Serializable
data class GetSealProgressResponse(
    val criteriaMet: Int,
    val criteriaTotal: Int,
) {
    fun toDomain() = SealProgress(
        criteriaMet = criteriaMet,
        criteriaTotal = criteriaTotal,
    )
}
