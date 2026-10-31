package com.aether.application.core.network

import com.aether.application.core.utils.AppLocale
import okhttp3.Interceptor
import okhttp3.Response

/** Tells the backend which language to answer in; Spring resolves it from Accept-Language. */
class AcceptLanguageInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("Accept-Language", AppLocale.current().toLanguageTag())
            .build()
        return chain.proceed(request)
    }
}
