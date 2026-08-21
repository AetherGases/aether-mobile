package com.aether.application.feature.auth.domain.repository

import com.aether.application.core.auth.model.Session

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Result<Session>
}
