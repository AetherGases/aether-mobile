package com.aether.application.feature.auth.data.remote

import com.aether.application.feature.auth.data.remote.dto.UserProfileResponse
import retrofit2.http.GET

// TODO: Kong has no /api/profile route yet, only /api/auth reaches ms-auth
private const val PROFILE = "api/profile"

interface ProfileApi {

    @GET(PROFILE)
    suspend fun getUserProfile(): UserProfileResponse
}
