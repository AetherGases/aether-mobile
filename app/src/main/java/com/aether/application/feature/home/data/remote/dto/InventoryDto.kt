package com.aether.application.feature.home.data.remote.dto

import com.aether.application.core.serialization.LocalDateSerializer
import com.aether.application.core.serialization.LocalDateTimeSerializer
import com.aether.application.feature.home.domain.model.Inventory
import com.aether.application.feature.home.domain.model.ReportingPeriod
import com.aether.application.feature.home.domain.model.inventoryStatusOf
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Item of the mine, pending and reviewed lists. The user's own list has no author, and
 * none of the lists send the period.
 */
@Serializable
data class InventoryDto(
    val id: Long,
    val name: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val author: String? = null,
    val authorImageUrl: String? = null,
    // TODO: the backend doesn't send the reviewer yet
    val reviewer: String? = null,
    val status: String,
    val type: String,
) {
    fun toDomain() = Inventory(
        id = id,
        name = name,
        createdAt = createdAt,
        period = null,
        author = author,
        authorImageUrl = authorImageUrl,
        reviewer = reviewer,
        status = inventoryStatusOf(status, type),
    )
}

/** Response of the `mine/last` and `reviewed/last` routes. */
@Serializable
data class LastInventoryDto(
    val id: Long,
    val name: String,
    val author: AuthorDto? = null,
    val period: PeriodDto? = null,
    val status: String,
    val type: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime? = null,
) {
    @Serializable
    data class AuthorDto(
        val name: String,
        val imageUrl: String? = null,
    )

    @Serializable
    data class PeriodDto(
        @Serializable(with = LocalDateSerializer::class)
        val start: LocalDate? = null,
        @Serializable(with = LocalDateSerializer::class)
        val end: LocalDate? = null,
    )

    fun toDomain() = Inventory(
        id = id,
        name = name,
        createdAt = createdAt,
        period = period?.let {
            if (it.start != null && it.end != null) ReportingPeriod(it.start, it.end) else null
        },
        author = author?.name,
        authorImageUrl = author?.imageUrl,
        reviewer = null,
        status = inventoryStatusOf(status, type),
    )
}
