package com.aether.application.feature.auth.data.storage

import com.aether.application.core.auth.data.SessionStorage
import com.aether.application.core.auth.model.Session
import com.aether.application.core.auth.storage.SessionManager
import java.time.Instant
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManagerImpl(
    private val sessionStorage: SessionStorage
): SessionManager {
    private var session: Session? = null
    private val _authState = MutableStateFlow(false)
    override val authState: StateFlow<Boolean> = _authState.asStateFlow()

    override suspend fun restoreSession() {
        session = sessionStorage.get()
        _authState.value = isAuthenticated()
    }

    override fun getSession(): Session? {
        return session
    }

    override suspend fun isAuthenticated(): Boolean {
        val currentSession = session ?: return false
        if (currentSession.expiration.isBefore(Instant.now())) {
            logout()
            return false
        }
        return true
    }

    override suspend fun hasPermission(name: String): Boolean {
        return session?.permissions.orEmpty().any { it.name == name }
    }

    override suspend fun save(session: Session): Boolean {
        return try {
            sessionStorage.save(session)
            this.session = session
            _authState.value = true
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun logout() {
        sessionStorage.clear()
        session = null
        _authState.value = false
    }
}