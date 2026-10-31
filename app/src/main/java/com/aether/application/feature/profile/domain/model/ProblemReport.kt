package com.aether.application.feature.profile.domain.model

const val MAX_PROBLEM_DESCRIPTION_LENGTH = 100

/** A PROBLEM_CATEGORY from the backend; [code] is what the report sends back, [label] is shown to the user. */
data class ProblemCategory(val code: String, val label: String)

/** Carries a message already written for the user. */
class ProblemException(message: String, cause: Throwable? = null) : Exception(message, cause)

private val SCREENSHOT_MIME_TYPES = mapOf(
    "png" to "image/png",
    "jpg" to "image/jpeg",
    "jpeg" to "image/jpeg",
)

fun screenshotMimeType(fileName: String): String? =
    SCREENSHOT_MIME_TYPES[fileName.substringAfterLast('.', "").lowercase()]
