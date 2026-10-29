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
 * Logs the result of every request, so any call shows which endpoint answered what and how
 * long it took. Failures (including the 404s the repositories read as "nothing yet") go to
 * warn/error, successes to debug. Only the path is logged (queries may carry tokens) and the
 * body only in debug builds.
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

        val tookMs = (System.nanoTime() - startedAt) / 1_000_000
        val message = buildString {
            append("$endpoint -> ${response.code} (${tookMs}ms)")
            if (BuildConfig.DEBUG) {
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
