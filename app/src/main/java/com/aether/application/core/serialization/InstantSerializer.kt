package com.aether.application.core.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

/**
 * Serializes [Instant] as its ISO-8601 string form (e.g. "2026-08-21T10:15:30Z").
 * kotlinx.serialization has no built-in serializer for java.time types, so any
 * @Serializable class holding an Instant must reference this explicitly:
 * `@Serializable(with = InstantSerializer::class)`.
 *
 * Also accepts an explicit offset ("2026-08-21T10:15:30.000+00:00"), which is how Jackson writes
 * java.util.Date and which Instant.parse rejects before Java 12 (older Android versions).
 */
object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        val value = decoder.decodeString()
        return try {
            Instant.parse(value)
        } catch (e: DateTimeParseException) {
            OffsetDateTime.parse(value).toInstant()
        }
    }
}
