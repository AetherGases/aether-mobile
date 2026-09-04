package com.aether.application.core.network

import com.aether.application.core.auth.storage.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val session = sessionManager.getSession()
        val request = chain.request()

        val authenticatedRequest = request.newBuilder()
            .apply {
                session?.accessToken?.let {
                    header("Authorization", "Bearer $it")
                }
            }
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
