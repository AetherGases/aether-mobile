package com.aether.application.core.auth.storage

import com.aether.application.core.auth.model.Session

interface SessionManager {
    fun getSession(): Session?

    suspend fun isAuthenticated(): Boolean

    suspend fun hasPermission(name: String): Boolean

    suspend fun save(session: Session): Boolean

    suspend fun logout()
}
