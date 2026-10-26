package com.aether.application.feature.auth.data.remote

import com.aether.application.feature.auth.data.remote.dto.UserProfileResponse
import com.aether.application.feature.profile.data.remote.dto.UpdateProfileRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

// TODO: Kong has no /api/profile route yet, only /api/auth reaches ms-auth
private const val PROFILE = "api/profile"

interface ProfileApi {

    @GET(PROFILE)
    suspend fun getUserProfile(): UserProfileResponse

    @PATCH(PROFILE)
    suspend fun updateProfile(@Body request: UpdateProfileRequest)
}
