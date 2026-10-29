package com.aether.application.core.network

import android.util.Log
import com.aether.application.core.auth.jwt.jwtExpiration
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

private const val TAG = "TokenAuthenticator"

class TokenAuthenticator(
    private val sessionManager: SessionManager,
    private val authApiProvider: () -> AuthApi
) : okhttp3.Authenticator {

    private val refreshMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Already retried with a fresh token: a 401 that a refresh didn't fix won't be fixed by another
        if (response.priorResponse != null) return null

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
                    // The refresh only returns tokens: keep the profile data (permissions, name, avatar)
                    sessionManager.save(
                        session.copy(
                            accessToken = loginResponse.accessToken,
                            refreshToken = loginResponse.refreshToken,
                            expiration = loginResponse.expiration,
                            refreshExpiration = jwtExpiration(loginResponse.refreshToken)
                        )
                    )
                    loginResponse.accessToken
                } catch (e: HttpException) {
                    // ms-auth answers an invalid, expired or reused refresh token with 401 (400 for an
                    // inactive user). A 5xx comes from the server/gateway and doesn't invalidate the session
                    if (e.code() in 400..499) {
                        Log.w(TAG, "Token refresh rejected (${e.code()}), logging out", e)
                        sessionManager.logout()
                    } else {
                        Log.w(TAG, "Token refresh failed on the server (${e.code()}), keeping the session", e)
                    }
                    null
                } catch (e: IOException) {
                    Log.w(TAG, "Token refresh failed", e)
                    null
                }
            }
        } ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $refreshedAccessToken")
            .build()
    }
}
