package com.aether.application.feature.auth.domain.exception

sealed class AuthException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class InvalidCredentials : AuthException(
        "Email ou senha inválidos."
    )

    class Network(
        cause: Throwable
    ) : AuthException(
        "Não foi possível conectar ao servidor.",
        cause
    )

    class Unexpected(
        cause: Throwable
    ) : AuthException(
        "Ocorreu um erro inesperado. Tente novamente mais tarde.",
        cause
    )
}