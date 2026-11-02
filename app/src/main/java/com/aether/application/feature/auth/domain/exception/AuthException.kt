package com.aether.application.feature.auth.domain.exception

import com.aether.application.R
import com.aether.application.core.utils.UiText
import com.aether.application.core.utils.UserFacingException
import com.aether.application.core.utils.uiText


sealed class AuthException(
    uiText: UiText,
    cause: Throwable? = null
) : UserFacingException(uiText, cause) {

    class InvalidCredentials : AuthException(
        uiText(R.string.auth_error_invalid_credentials)
    )

    class Api(
        message: String
    ) : AuthException(uiText(message))

    class Network(
        cause: Throwable
    ) : AuthException(
        uiText(R.string.common_error_server_unreachable),
        cause
    )

    class Unexpected(
        cause: Throwable
    ) : AuthException(
        uiText(R.string.common_unexpected_error),
        cause
    )
}
