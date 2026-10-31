package com.aether.application.feature.auth.data.remote

import com.aether.application.feature.auth.data.remote.dto.UserProfileResponse
import com.aether.application.feature.profile.data.remote.dto.ProblemCategoryResponse
import com.aether.application.feature.profile.data.remote.dto.ReportProblemRequest
import com.aether.application.feature.profile.data.remote.dto.ReportProblemResponse
import com.aether.application.feature.profile.data.remote.dto.UpdateProfileRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

private const val PROFILE = "api/profile"

interface ProfileApi {

    @GET(PROFILE)
    suspend fun getUserProfile(): UserProfileResponse

    @PATCH(PROFILE)
    suspend fun updateProfile(@Body request: UpdateProfileRequest)

    /** Categories a problem can be reported under (PROBLEM_CATEGORY), with their display labels. */
    @GET("$PROFILE/problem/categories")
    suspend fun getProblemCategories(): List<ProblemCategoryResponse>

    @POST("$PROFILE/problem")
    suspend fun reportProblem(@Body request: ReportProblemRequest): ReportProblemResponse
}
