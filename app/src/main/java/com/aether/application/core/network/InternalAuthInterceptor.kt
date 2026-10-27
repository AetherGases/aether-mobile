package com.aether.application.core.network

import okhttp3.Interceptor
import okhttp3.Response

class InternalAuthInterceptor(
    private val internalAuthenticationKey: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("Authorization", internalAuthenticationKey)
            .build()

        return chain.proceed(request)
    }
}
