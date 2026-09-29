package com.aether.application.feature.auth.domain.model

const val PASSWORD_REQUIREMENTS_MESSAGE = "A senha não atende aos requisitos."

class PasswordRule(
    val label: String,
    val isSatisfiedBy: (String) -> Boolean,
)

val DefaultPasswordRules = listOf(
    PasswordRule("8-28 caracteres") { it.length in 8..28 },
    PasswordRule("Pelo menos uma letra maiúscula") { pw -> pw.any { it.isUpperCase() } },
    PasswordRule("Pelo menos uma letra minúscula") { pw -> pw.any { it.isLowerCase() } },
    PasswordRule("Pelo menos um número") { pw -> pw.any { it.isDigit() } },
)

fun String.meetsPasswordRules(): Boolean = DefaultPasswordRules.all { it.isSatisfiedBy(this) }