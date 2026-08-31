package com.aether.application.feature.auth.domain.usecase

import com.aether.application.core.auth.model.Session
import com.aether.application.feature.auth.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<Session> {

        val trimmedEmail = email.trim()

        // Empty fields
        if (trimmedEmail.isBlank())
            return Result.failure(IllegalArgumentException("Email não pode estar vazio!"))

        if (password.isBlank())
            return Result.failure(IllegalArgumentException("Senha não pode estar vazia!"))

        // Email
        if (trimmedEmail.length > 254)
            return Result.failure(IllegalArgumentException("Email não pode possuir mais de 254 caracteres!"))

        if (trimmedEmail.contains(" "))
            return Result.failure(IllegalArgumentException("Email não pode possuir espaços!"))

        val emailRegex = Regex("^[A-Za-z0-9.!#\$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+$")

        if (!emailRegex.matches(trimmedEmail))
            return Result.failure(IllegalArgumentException("Sintaxe do email é inválida!"))

        // Password
        if (password.length < 8)
            return Result.failure(IllegalArgumentException("Senha deve possuir pelo menos 8 caracteres!"))

        if (password.length > 128)
            return Result.failure(IllegalArgumentException("Senha não pode possuir mais de 128 caracteres!"))

        if (!password.any { it.isUpperCase() })
            return Result.failure(IllegalArgumentException("Senha deve possuir pelo menos uma letra maiúscula!"))

        if (!password.any { it.isLowerCase() })
            return Result.failure(IllegalArgumentException("Senha deve possuir pelo menos uma letra minúscula!"))

        if (!password.any { it.isDigit() })
            return Result.failure(IllegalArgumentException("Senha deve possuir pelo menos um número!"))

        if (!password.any { !it.isLetterOrDigit() })
            return Result.failure(IllegalArgumentException("Senha deve possuir pelo menos um caractere especial!"))

        return authRepository.login(trimmedEmail, password)
    }
}
