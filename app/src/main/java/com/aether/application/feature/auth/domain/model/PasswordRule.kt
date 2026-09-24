package com.aether.application.feature.auth.domain.model

class PasswordRule(
    val label: String,
    val isSatisfiedBy: (String) -> Boolean,
)

val DefaultPasswordRules = listOf(
    PasswordRule("8 caracteres no mínimo") { it.length >= 8 },
    PasswordRule("Pelo menos uma letra maiúscula") { pw -> pw.any { it.isUpperCase() } },
    PasswordRule("Pelo menos um número") { pw -> pw.any { it.isDigit() } },
)