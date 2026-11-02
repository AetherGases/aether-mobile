package com.aether.application.core.network

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class BaseUrlInterceptor(
    private val serverConfigCache: ServerConfigCache
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val override = serverConfigCache.selectedDomain.value?.toHttpUrlOrNull()
            ?: return chain.proceed(request)

        val newUrl = request.url.newBuilder()
            .scheme(override.scheme)
            .host(override.host)
            .port(override.port)
            .build()

        return chain.proceed(request.newBuilder().url(newUrl).build())
    }
}
