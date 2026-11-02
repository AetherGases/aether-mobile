package com.aether.application.core.notification.domain

interface FcmTokenStorage {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
}