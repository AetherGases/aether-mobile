package com.aether.application.core.utils

import com.aether.application.R


/** A failure whose [uiText] is safe to show to the user as is. */
open class UserFacingException(val uiText: UiText, cause: Throwable? = null) :
    Exception((uiText as? UiText.Dynamic)?.value, cause)

/** Only failures built as [UserFacingException] are shown as they are; anything else is generic. */
fun Throwable.userMessage(): UiText =
    (this as? UserFacingException)?.uiText ?: uiText(R.string.common_unexpected_error)
