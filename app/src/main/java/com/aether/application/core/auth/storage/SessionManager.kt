package com.aether.application.core.auth.storage

import com.aether.application.core.auth.model.Session
import com.aether.application.core.domain.model.UserRole
import kotlinx.coroutines.flow.StateFlow

interface SessionManager {
    val authState: StateFlow<Boolean>

    fun getSession(): Session?

    suspend fun isAuthenticated(): Boolean

    suspend fun save(session: Session): Boolean

    suspend fun getUserRole(): UserRole?

    suspend fun logout()
}
