package com.aether.application.core.auth.data

import com.aether.application.core.auth.model.Session

interface SessionStorage {

    suspend fun save(session: Session)

    suspend fun get(): Session?

    suspend fun clear()
}
