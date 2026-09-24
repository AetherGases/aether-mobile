package com.aether.application.feature.auth.data.storage

import com.aether.application.core.auth.data.SessionStorage
import com.aether.application.core.auth.model.Session
import com.aether.application.core.auth.storage.SessionManager

class SessionManagerImpl(
    private val sessionStorage: SessionStorage
): SessionManager {
    private var session: Session? = null

    suspend fun init() {
        session = sessionStorage.get()
    }

    override fun getSession(): Session? {
        return session
    }

    override suspend fun isAuthenticated(): Boolean {
        return session != null
    }

    override suspend fun hasPermission(name: String): Boolean {
        return session?.permissions.orEmpty().any { it.name == name }
    }

    override suspend fun save(session: Session): Boolean {
        return try {
            sessionStorage.save(session)
            this.session = session
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun logout() {
        sessionStorage.clear()
        session = null
    }
}
