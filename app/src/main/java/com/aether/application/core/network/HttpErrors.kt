package com.aether.application.core.network

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import retrofit2.HttpException

private val errorJson = Json { ignoreUnknownKeys = true }

fun HttpException.readErrorMessage(): String? {
    val body = response()?.errorBody()?.string() ?: return null

    return try {
        errorJson.decodeFromString<ErrorResponse>(body).message
    } catch (_: SerializationException) {
        null
    }
}
