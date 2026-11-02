package com.aether.application.core.auth.jwt

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.time.Instant
import java.util.Base64

/**
 * Reads the `exp` claim of a JWT without verifying it, or null when the token can't be decoded.
 * The refresh endpoint only returns the access token expiration, so this is how the refresh
 * token's own expiration is known.
 */
fun jwtExpiration(token: String): Instant? {
    return try {
        val payload = token.removePrefix("Bearer ").split(".").getOrNull(1) ?: return null
        val json = String(Base64.getUrlDecoder().decode(payload.trimEnd('=')))
        val exp = Json.parseToJsonElement(json).jsonObject["exp"]?.jsonPrimitive?.longOrNull ?: return null
        Instant.ofEpochSecond(exp)
    } catch (e: IllegalArgumentException) {
        null
    }
}
