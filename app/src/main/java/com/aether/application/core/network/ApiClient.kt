package com.aether.application.core.network

import com.aether.application.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import retrofit2.Retrofit

private const val READ_TIMEOUT_SECONDS = 90L

object ApiClient {
    /** Fields the backend adds later must not break the DTOs that don't declare them yet. */
    val json = Json { ignoreUnknownKeys = true }

    fun getOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        baseUrlInterceptor: BaseUrlInterceptor?,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                if (baseUrlInterceptor != null) {
                    addInterceptor(baseUrlInterceptor)
                }
            }
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            // The AI hub only answers after the whole agent run, which outlasts OkHttp's 10s default
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    fun getRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }
}