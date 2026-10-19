package com.aether.application.feature.reports.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * How the emissions of the company's operations are consolidated in a report (GHG Protocol).
 * The backend stores free text, so these three values exist only in the app. The
 * [SerialName] is what gets sent and stays fixed even if the on-screen [label] changes.
 */
@Serializable
enum class ConsolidationApproach(val label: String) {
    @SerialName("Participação societária")
    EQUITY_SHARE("Participação societária"),

    @SerialName("Controle financeiro")
    FINANCIAL_CONTROL("Controle financeiro"),

    @SerialName("Controle operacional")
    OPERATIONAL_CONTROL("Controle operacional"),
}

data class CreatedReport(
    val id: Long,
    val name: String,
)

/** A failure with a message that is safe to show to the user. */
class ReportException(message: String, cause: Throwable? = null) : Exception(message, cause)

private const val NAME_MIN_LENGTH = 3
private const val NAME_MAX_LENGTH = 150
private const val SPREADSHEET_EXTENSION = ".xlsx"

// Same rule as the backend: letters separated by spaces, so no digits, hyphens or punctuation
private val NAME_REGEX = Regex("^[A-Za-zÀ-ÖØ-öø-ÿ]+(?:\\s+[A-Za-zÀ-ÖØ-öø-ÿ]+)*$")

/** Returns why [name] would be rejected by the backend, or null when it is valid. */
fun validateReportName(name: String): String? {
    val trimmed = name.trim()
    return when {
        trimmed.length < NAME_MIN_LENGTH -> "O nome precisa ter pelo menos $NAME_MIN_LENGTH letras."
        trimmed.length > NAME_MAX_LENGTH -> "O nome pode ter no máximo $NAME_MAX_LENGTH caracteres."
        !NAME_REGEX.matches(trimmed) -> "Use apenas letras e espaços, sem números ou símbolos."
        else -> null
    }
}

fun isSpreadsheetFileName(fileName: String): Boolean =
    fileName.endsWith(SPREADSHEET_EXTENSION, ignoreCase = true)
