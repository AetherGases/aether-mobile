package com.aether.application.feature.auth.data.remote

import com.aether.application.feature.auth.data.remote.dto.UserProfileResponse
import com.aether.application.feature.profile.data.remote.dto.UpdateProfileRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

private const val PROFILE = "profile"

interface ProfileApi {

    @GET(PROFILE)
    suspend fun getUserProfile(): UserProfileResponse

    @PATCH(PROFILE)
    suspend fun updateProfile(@Body request: UpdateProfileRequest)
}
