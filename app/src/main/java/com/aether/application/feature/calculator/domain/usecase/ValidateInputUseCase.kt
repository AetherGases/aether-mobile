package com.aether.application.feature.calculator.domain.usecase

/** Parses what the user typed on the calculator steps; null means the value is not usable. */
class ValidateInputUseCase {

    // Accepts both "1.700,5" (pt-BR) and "1700.5"
    fun parseVolume(raw: String): Double? {
        val text = raw.trim().replace(" ", "")
        val normalized = if (',' in text) text.replace(".", "").replace(',', '.') else text
        return normalized.toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0 }
    }

    fun parseYears(raw: String): Int? = raw.trim().toIntOrNull()?.takeIf { it >= 1 }

    fun parseScope(label: String): Int? = label.trim().removePrefix("Escopo").trim().toIntOrNull()
        ?.takeIf { it in 1..3 }
}
