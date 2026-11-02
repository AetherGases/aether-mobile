package com.aether.application.core.utils

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource

/**
 * User-facing text that can be built outside the UI layer (ViewModels, repositories, exceptions)
 * without holding a [Context]. It is resolved against string resources when it is displayed.
 */
sealed interface UiText {
    data class Dynamic(val value: String) : UiText

    class Resource(@param:StringRes val id: Int, vararg val args: Any) : UiText {
        override fun equals(other: Any?): Boolean =
            other is Resource && id == other.id && args.contentEquals(other.args)

        override fun hashCode(): Int = 31 * id + args.contentHashCode()
    }

    data class Plural(@param:PluralsRes val id: Int, val count: Int) : UiText

    @Composable
    fun asString(): String = when (this) {
        is Dynamic -> value
        is Resource -> stringResource(id, *args)
        is Plural -> pluralStringResource(id, count, count)
    }

    fun asString(context: Context): String = when (this) {
        is Dynamic -> value
        is Resource -> context.getString(id, *args)
        is Plural -> context.resources.getQuantityString(id, count, count)
    }
}

fun uiText(@StringRes id: Int, vararg args: Any): UiText = UiText.Resource(id, *args)

fun uiText(value: String): UiText = UiText.Dynamic(value)

fun uiPlural(@PluralsRes id: Int, count: Int): UiText = UiText.Plural(id, count)
