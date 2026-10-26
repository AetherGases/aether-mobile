package com.aether.application.core.network

import android.util.Log
import com.aether.application.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

private const val TAG = "HttpError"
private const val MAX_LOGGED_BODY_BYTES = 1024L

/**
 * Logs every request that doesn't end in a 2xx, including the 404s the repositories read as
 * "nothing yet", so a failure always shows which endpoint answered what. Only the path is
 * logged (queries may carry tokens) and the error body only in debug builds.
 * As an application interceptor it sees the final response, after the token refresh retried.
 */
class HttpErrorLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val endpoint = "${request.method} ${request.url.encodedPath}"

        val response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            // A cancelled call is the caller leaving, not a failure
            if (!chain.call().isCanceled()) Log.w(TAG, "$endpoint failed: ${e.message}", e)
            throw e
        }

        if (!response.isSuccessful) {
            val message = buildString {
                append("$endpoint -> ${response.code}")
                if (BuildConfig.DEBUG) {
                    val body = response.peekBody(MAX_LOGGED_BODY_BYTES).string()
                    if (body.isNotBlank()) append(": ").append(body)
                }
            }
            if (response.code >= 500) Log.e(TAG, message) else Log.w(TAG, message)
        }
        return response
    }
}
