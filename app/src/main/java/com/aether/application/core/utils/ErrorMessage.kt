package com.aether.application.core.utils

const val DEFAULT_ERROR_MESSAGE = "Ocorreu um erro inesperado. Tente novamente mais tarde."

fun Throwable.userMessage(): String {
    val message = message
    return if (message.isNullOrBlank()) DEFAULT_ERROR_MESSAGE else message
}
