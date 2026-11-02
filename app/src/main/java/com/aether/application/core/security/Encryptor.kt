package com.aether.application.core.security

interface Encryptor {
    fun encrypt(value: String): String
    fun decrypt(value: String): String
}