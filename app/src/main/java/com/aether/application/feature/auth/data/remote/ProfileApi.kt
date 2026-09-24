package com.aether.application.feature.auth.data.remote

import com.aether.application.feature.auth.data.remote.dto.UserProfileResponse
import retrofit2.http.GET

private const val PROFILE = "profile"

interface ProfileApi {

    @GET(PROFILE)
    suspend fun getUserProfile(): UserProfileResponse
}
