package com.aether.application.core.network

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import retrofit2.HttpException

private val errorJson = Json { ignoreUnknownKeys = true }

fun HttpException.readErrorMessage(): String? {
    val body = response()?.errorBody()?.string() ?: return null

    return try {
        // Spring's default error body carries an empty message, which is no use to show
        errorJson.decodeFromString<ErrorResponse>(body).message?.takeIf { it.isNotBlank() }
    } catch (_: SerializationException) {
        null
    }
}
