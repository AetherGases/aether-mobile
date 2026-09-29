package com.aether.application.feature.auth.domain.exception

import com.aether.application.core.utils.DEFAULT_ERROR_MESSAGE

sealed class AuthException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class InvalidCredentials : AuthException(
        "Email ou senha inválidos."
    )

    class Api(
        message: String
    ) : AuthException(message)

    class Network(
        cause: Throwable
    ) : AuthException(
        "Não foi possível conectar ao servidor.",
        cause
    )

    class Unexpected(
        cause: Throwable
    ) : AuthException(
        DEFAULT_ERROR_MESSAGE,
        cause
    )
}
