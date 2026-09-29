package com.aether.application.feature.auth.domain.model

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