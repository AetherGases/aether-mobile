package com.aether.application.feature.auth.domain.model

import androidx.annotation.StringRes
import com.aether.application.R


class PasswordRule(
    @StringRes val labelRes: Int,
    val isSatisfiedBy: (String) -> Boolean,
)

val DefaultPasswordRules = listOf(
    PasswordRule(R.string.auth_password_rule_length) { it.length in 8..28 },
    PasswordRule(R.string.auth_password_rule_uppercase) { pw -> pw.any { it.isUpperCase() } },
    PasswordRule(R.string.auth_password_rule_lowercase) { pw -> pw.any { it.isLowerCase() } },
    PasswordRule(R.string.auth_password_rule_number) { pw -> pw.any { it.isDigit() } },
)

fun String.meetsPasswordRules(): Boolean = DefaultPasswordRules.all { it.isSatisfiedBy(this) }
