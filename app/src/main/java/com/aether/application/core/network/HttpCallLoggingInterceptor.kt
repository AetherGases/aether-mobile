package com.aether.application.core.network

import android.util.Log
import com.aether.application.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

private const val TAG = "HttpCall"
// Logcat truncates lines past ~4000 chars
private const val MAX_LOGGED_BODY_BYTES = 3500L

/**
 * Logs failed requests (including the 404s the repositories read as "nothing yet") to warn/error,
 * so a failure always shows which endpoint answered what. Debug (QA) builds also log successful
 * calls at debug level, with how long each call took. Only the path is logged (queries may carry
 * tokens) and the body only in debug builds.
 * As an application interceptor it sees the final response, after the token refresh retried.
 */
class HttpCallLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val endpoint = "${request.method} ${request.url.encodedPath}"
        val startedAt = System.nanoTime()

        val response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            // A cancelled call is the caller leaving, not a failure
            if (!chain.call().isCanceled()) Log.w(TAG, "$endpoint failed: ${e.message}", e)
            throw e
        }

        if (!BuildConfig.DEBUG && response.isSuccessful) return response

        val tookMs = (System.nanoTime() - startedAt) / 1_000_000
        val message = buildString {
            append("$endpoint -> ${response.code}")
            if (BuildConfig.DEBUG) {
                append(" (${tookMs}ms)")
                val body = response.peekBody(MAX_LOGGED_BODY_BYTES).string()
                if (body.isNotBlank()) append(": ").append(body)
            }
        }
        when {
            response.code >= 500 -> Log.e(TAG, message)
            !response.isSuccessful -> Log.w(TAG, message)
            else -> Log.d(TAG, message)
        }
        return response
    }
}
