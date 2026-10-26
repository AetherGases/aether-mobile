package com.aether.application.core.auth.storage

import com.aether.application.core.auth.model.Session
import com.aether.application.feature.auth.domain.model.AppPermission
import kotlinx.coroutines.flow.StateFlow

interface SessionManager {
    val authState: StateFlow<Boolean>

    fun getSession(): Session?

    suspend fun isAuthenticated(): Boolean

    suspend fun hasPermission(permission: AppPermission): Boolean

    suspend fun save(session: Session): Boolean

    suspend fun restoreSession()

    suspend fun logout()
}
