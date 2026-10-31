package com.aether.application.feature.reports.domain.model

import androidx.annotation.StringRes
import com.aether.application.R
import com.aether.application.core.utils.UiText
import com.aether.application.core.utils.UserFacingException
import com.aether.application.core.utils.uiText
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * How the emissions of the company's operations are consolidated in a report (GHG Protocol).
 * The backend stores free text, so these three values exist only in the app. The
 * [SerialName] is what gets sent and stays fixed even if the on-screen [labelRes] changes.
 */
@Serializable
enum class ConsolidationApproach(@StringRes val labelRes: Int) {
    @SerialName("Participação societária")
    EQUITY_SHARE(R.string.reports_approach_equity_share),

    @SerialName("Controle financeiro")
    FINANCIAL_CONTROL(R.string.reports_approach_financial_control),

    @SerialName("Controle operacional")
    OPERATIONAL_CONTROL(R.string.reports_approach_operational_control),
}

data class CreatedReport(
    val id: Long,
    val name: String,
)

/** A failure with a message that is safe to show to the user. */
class ReportException(uiText: UiText, cause: Throwable? = null) : UserFacingException(uiText, cause)

private const val NAME_MIN_LENGTH = 3
private const val NAME_MAX_LENGTH = 150
private const val SPREADSHEET_EXTENSION = ".xlsx"

// Same rule as the backend: letters separated by spaces, so no digits, hyphens or punctuation
private val NAME_REGEX = Regex("^[A-Za-zÀ-ÖØ-öø-ÿ]+(?:\\s+[A-Za-zÀ-ÖØ-öø-ÿ]+)*$")

/** Returns why [name] would be rejected by the backend, or null when it is valid. */
fun validateReportName(name: String): UiText? {
    val trimmed = name.trim()
    return when {
        trimmed.length < NAME_MIN_LENGTH -> uiText(R.string.reports_name_too_short, NAME_MIN_LENGTH)
        trimmed.length > NAME_MAX_LENGTH -> uiText(R.string.reports_name_too_long, NAME_MAX_LENGTH)
        !NAME_REGEX.matches(trimmed) -> uiText(R.string.reports_name_invalid_characters)
        else -> null
    }
}

fun isSpreadsheetFileName(fileName: String): Boolean =
    fileName.endsWith(SPREADSHEET_EXTENSION, ignoreCase = true)
