package com.aether.application.core.auth.storage

import com.aether.application.core.auth.model.Session
import com.aether.application.feature.auth.domain.model.AppPermission
import kotlinx.coroutines.flow.StateFlow

interface SessionManager {
    val authState: StateFlow<Boolean>

    fun getSession(): Session?

    suspend fun isAuthenticated(): Boolean

    suspend fun hasPermission(permission: AppPermission): Boolean

    /**
     * [remember] says whether the session survives an app restart: false keeps the tokens only in
     * memory. Null keeps the choice of the current session (a refresh, an avatar change).
     */
    suspend fun save(session: Session, remember: Boolean? = null): Boolean

    suspend fun restoreSession()

    suspend fun logout()
}
