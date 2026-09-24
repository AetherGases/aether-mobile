package com.aether.application.feature.auth.data.storage

import com.aether.application.core.auth.data.SessionStorage
import com.aether.application.core.auth.model.Session
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.core.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManagerImpl(
    private val sessionStorage: SessionStorage
): SessionManager {
    private var session: Session? = null

    private val _authState = MutableStateFlow(false)
    override val authState: StateFlow<Boolean> = _authState.asStateFlow()

    suspend fun init() {
        session = sessionStorage.get()
        _authState.value = session != null
    }

    override fun getSession(): Session? {
        return session
    }

    override suspend fun isAuthenticated(): Boolean {
        return session != null
    }

    override suspend fun save(session: Session): Boolean {
        return try {
            sessionStorage.save(session)
            this.session = session
            _authState.value = true
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getUserRole(): UserRole? {
        // TODO: LoginResponse/Session carries no role field yet — derive this once
        // the backend contract includes one (or decode it from the access token).
        TODO("Not yet implemented")
    }

    override suspend fun logout() {
        sessionStorage.clear()
        session = null
        _authState.value = false
    }
}
