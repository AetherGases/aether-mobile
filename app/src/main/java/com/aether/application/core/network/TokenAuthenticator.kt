package com.aether.application.core.network

import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.auth.data.remote.AuthApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException
import java.io.IOException

class TokenAuthenticator(
    private val sessionManager: SessionManager,
    private val authApiProvider: () -> AuthApi
) : okhttp3.Authenticator {

    private val refreshMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) > 3) return null

        val failedAccessToken = response.request.header("Authorization")

        val refreshedAccessToken = runBlocking {
            refreshMutex.withLock {
                val session = sessionManager.getSession() ?: return@withLock null

                if (failedAccessToken != null && failedAccessToken != "Bearer ${session.accessToken}") {
                    return@withLock session.accessToken
                }

                try {
                    val loginResponse = authApiProvider().refreshToken(
                        email = session.email,
                        refreshToken = "Bearer ${session.refreshToken}"
                    )
                    sessionManager.save(loginResponse.toDomain())
                    loginResponse.accessToken
                } catch (e: HttpException) {
                    sessionManager.logout()
                    null
                } catch (e: IOException) {
                    null
                }
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
