package com.aether.application.feature.home.domain.model

import com.aether.application.core.serialization.LocalDateSerializer
import com.aether.application.core.serialization.LocalDateTimeSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class Inventory(
    val id: Long,
    val name: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val period: ReportingPeriod,
    val author: String?,
    val authorImageUrl: String?,
    @Serializable(with = InventoryStatusSerializer::class)
    val status: InventoryStatus,
)

@Serializable
data class ReportingPeriod(
    @Serializable(with = LocalDateSerializer::class)
    val start: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val end: LocalDate
)

sealed interface InventoryStatus {

    enum class Input : InventoryStatus {
        UNDER_REVIEW,
        APPROVED,
        REJECTED,
        PROCESSING,
        PROCESSING_FAILED,
    }

    enum class Output : InventoryStatus {
        REQUESTED,
        ISSUED,
        PROCESSING_FAILED,
    }
}

/**
 * Serializes an [InventoryStatus] as its plain enum name.
 * TODO: confirm with the backend; PROCESSING_FAILED exists in both enums and is
 *  always decoded as [InventoryStatus.Input.PROCESSING_FAILED].
 */
object InventoryStatusSerializer : KSerializer<InventoryStatus> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("InventoryStatus", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: InventoryStatus) {
        encoder.encodeString((value as Enum<*>).name)
    }

    override fun deserialize(decoder: Decoder): InventoryStatus {
        val name = decoder.decodeString()
        return InventoryStatus.Input.entries.firstOrNull { it.name == name }
            ?: InventoryStatus.Output.entries.firstOrNull { it.name == name }
            ?: throw IllegalArgumentException("Unknown inventory status: $name")
    }
}
