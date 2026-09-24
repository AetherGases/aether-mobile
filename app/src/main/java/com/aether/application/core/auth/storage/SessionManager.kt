package com.aether.application.core.auth.storage

import com.aether.application.core.auth.model.Session
import com.aether.application.core.domain.model.UserRole

interface SessionManager {
    fun getSession(): Session?

    suspend fun isAuthenticated(): Boolean

    suspend fun save(session: Session): Boolean

    suspend fun getUserRole(): UserRole?

    suspend fun restoreSession()

    suspend fun logout()
}
