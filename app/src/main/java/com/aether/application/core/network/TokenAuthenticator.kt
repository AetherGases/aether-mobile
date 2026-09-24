package com.aether.application.core.network

import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.auth.data.remote.AuthApi
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val sessionManager: SessionManager,
    private val authApiProvider: () -> AuthApi
) : okhttp3.Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) > 2) return null

        val session = sessionManager.getSession() ?: return null

        val refreshedAccessToken = runBlocking {
            try {
                val loginResponse = authApiProvider().refreshToken(
                    email = session.email,
                    refreshToken = "Bearer ${session.refreshToken}"
                )
                sessionManager.save(loginResponse.toDomain())
                loginResponse.accessToken
            } catch (e: Exception) {
                sessionManager.logout()
                null
            }
        } ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $refreshedAccessToken")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
